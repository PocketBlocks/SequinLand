package net.pocketdreams.sequinland.notchian;

import org.spacehq.packetlib.Server;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.ServerLoginHandler;
import org.spacehq.mc.protocol.data.message.TextMessage;
import org.spacehq.mc.protocol.data.status.PlayerInfo;
import org.spacehq.mc.protocol.data.status.ServerStatusInfo;
import org.spacehq.mc.protocol.data.status.VersionInfo;
import org.spacehq.mc.protocol.data.status.handler.ServerInfoBuilder;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.event.server.ServerAdapter;
import org.spacehq.packetlib.event.server.SessionAddedEvent;
import org.spacehq.packetlib.event.server.SessionRemovedEvent;
import org.spacehq.packetlib.event.session.PacketReceivedEvent;
import org.spacehq.packetlib.event.session.SessionAdapter;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.NotchPacketTranslator;

public class NotchianServer {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 25565;
    private static final Proxy PROXY = Proxy.NO_PROXY;
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;

    private final cn.nukkit.Server nukkitServer;
    
    public NotchianServer(cn.nukkit.Server server) {
        this.nukkitServer = server;
    }

    // TODO: Store on the player class
    private ConcurrentHashMap<Session, NotchianPlayer> sessions = new ConcurrentHashMap<>();
    
    public void start() {
        Server server = new Server(HOST, PORT, MinecraftProtocol.class, new TcpSessionFactory(PROXY));
        server.setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, AUTH_PROXY);
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, new ServerInfoBuilder() {
            @Override
            public ServerStatusInfo buildInfo(Session session) {
                return new ServerStatusInfo(new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION), new PlayerInfo(nukkitServer.getMaxPlayers(), nukkitServer.getOnlinePlayers().size(), new GameProfile[0]), new TextMessage(nukkitServer.getMotd()), null);
            }
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, new ServerLoginHandler() {
            @Override
            public void loggedIn(Session session) {
                RakNetInterface rakNetInterface = nukkitServer.getRakNetInterface();
                for (Entry<String, Object> entry : session.getFlags().entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                GameProfile profile = session.getFlag("profile");
                String username = profile.getName(); // Player username
                Skin skin = null;
                try {
                    skin = new Skin(ImageIO.read(new URL("http://skins.minecraft.net/MinecraftSkins/" + username + ".png")));
                } catch (IOException e) {
                    try {
                        skin = new Skin(ImageIO.read(new URL("http://skins.minecraft.net/MinecraftSkins/Shantae.png")));
                    } catch (IOException e1) {
                        // *gets award* You tried
                        e1.printStackTrace();
                        return;
                    }
                }
                int clientId = (int) (1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL));
                String identifier = String.valueOf(clientId);
                NotchianPlayer player = new NotchianPlayer(session, (long) clientId, session.getHost(), session.getPort());
                sessions.put(session, player);
                player.globalRakNetId = clientId;
                rakNetInterface.players.put(identifier, player);
                rakNetInterface.identifiersACK.put(identifier, 0);
                rakNetInterface.identifiers.put(player.rawHashCode(), identifier);
                nukkitServer.addPlayer(identifier, player);
                // After adding the player, we "fake" a LoginPacket
                LoginPacket pk = new LoginPacket();
                // We aren't going to write chain data, too much work
                pk.clientId = clientId;
                pk.clientUUID = profile.getId();
                pk.deviceModel = "Minecraft Java";
                pk.deviceOperatingSystem = 0;
                pk.gameEdition = 0;
                pk.identityPublicKey = "java";
                pk.protocol = ProtocolInfo.CURRENT_PROTOCOL;
                pk.serverAddress = "127.0.0.1";
                pk.skin = skin;
                pk.username = username;
                // And then we send it to Nukkit
                player.handleDataPacket(pk);
            }
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);
        server.addListener(new ServerAdapter() {
            @Override
            public void sessionAdded(SessionAddedEvent event) {
                event.getSession().addListener(new SessionAdapter() {
                    @Override
                    public void packetReceived(PacketReceivedEvent event) {
                        Class<? extends NotchPacketTranslator> clazz = NotchPacketTranslator.translators.get(event.getPacket().getClass());
                        if (clazz != null) {
                            try {
                                NotchPacketTranslator translator = (NotchPacketTranslator) clazz.newInstance();
                            
                                NotchianPlayer player = sessions.get(event.getSession());
                                DataPacket[] packets = translator.translate(event.getPacket(), player);
                                for (DataPacket packet : packets) {
                                    player.handleDataPacket(packet);
                                }
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            @Override
            public void sessionRemoved(SessionRemovedEvent event) {
                NotchianPlayer player = sessions.get(event.getSession());
                player.close("Disconnected", "Disconnected", true);
                sessions.remove(player);
            }
        });

        server.bind();
        
        while (true) {}
    }
}