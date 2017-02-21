package net.pocketdreams.sequinland.notchian;

import org.spacehq.packetlib.Server;

import java.io.File;
import java.net.Proxy;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.data.SubProtocol;
import org.spacehq.mc.protocol.ServerLoginHandler;
import org.spacehq.mc.protocol.data.game.entity.player.GameMode;
import org.spacehq.mc.protocol.data.game.setting.Difficulty;
import org.spacehq.mc.protocol.data.game.world.WorldType;
import org.spacehq.mc.protocol.data.message.TextMessage;
import org.spacehq.mc.protocol.data.status.PlayerInfo;
import org.spacehq.mc.protocol.data.status.ServerStatusInfo;
import org.spacehq.mc.protocol.data.status.VersionInfo;
import org.spacehq.mc.protocol.data.status.handler.ServerInfoBuilder;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.event.server.ServerAdapter;
import org.spacehq.packetlib.event.server.SessionAddedEvent;
import org.spacehq.packetlib.event.server.SessionRemovedEvent;
import org.spacehq.packetlib.event.session.PacketReceivedEvent;
import org.spacehq.packetlib.event.session.SessionAdapter;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.TextPacket;
import co.aikar.timings.Timings;
import net.pocketdreams.sequinland.notchian.client.NotchianClient;

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
    private WeakHashMap<Session, NotchianClient> sessions = new WeakHashMap<>();
    
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
                for (Entry<String, Object> entry : session.getFlags().entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                GameProfile profile = session.getFlag("profile");
                String username = profile.getName(); // Player username
                int clientId = 1;
                String identifier = String.valueOf(clientId);
                NotchianClient player = new NotchianClient(session, (long) clientId, session.getHost(), session.getPort());
                sessions.put(session, player);
                player.globalRakNetId = clientId;
                RakNetInterface.players.put(identifier, player);
                RakNetInterface.identifiersACK.put(identifier, 0);
                RakNetInterface.identifiers.put(player.rawHashCode(), identifier);
                RakNetInterface.instance.server.addPlayer(identifier, player);
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
                pk.skin = new Skin(new File("D:\\Minecraft Servers\\PocketDreams\\plugins\\TasselCitizens\\Shantae.png"));
                pk.username = username;
                // And then we send it to Nukkit
                player.handleDataPacket(pk);
                
                session.send(new ServerJoinGamePacket(0, false, GameMode.SURVIVAL, 0, Difficulty.PEACEFUL, 10, WorldType.DEFAULT, false));
                session.send(new ServerPlayerPositionRotationPacket(0, 128, 0, 0, 0, 0));
            }
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);
        server.addListener(new ServerAdapter() {
            @Override
            public void sessionAdded(SessionAddedEvent event) {
                event.getSession().addListener(new SessionAdapter() {
                    @Override
                    public void packetReceived(PacketReceivedEvent event) {
                        if(event.getPacket() instanceof ClientChatPacket) {
                            ClientChatPacket packet = event.getPacket();
                            /* ClientChatPacket packet = event.getPacket();
                            GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
                            System.out.println(profile.getName() + ": " + packet.getMessage());
                            Message msg = new TextMessage("Hello, ").setStyle(new MessageStyle().setColor(ChatColor.GREEN));
                            Message name = new TextMessage(profile.getName()).setStyle(new MessageStyle().setColor(ChatColor.AQUA).addFormat(ChatFormat.UNDERLINED));
                            Message end = new TextMessage("!");
                            msg.addExtra(name);
                            msg.addExtra(end);
                            event.getSession().send(new ServerChatPacket(msg)); */
                            NotchianClient notch = sessions.get(event.getSession());
                            if (packet.getMessage().startsWith("/")) {
                                PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(notch, "/" + packet.getMessage().substring(1));
                                cn.nukkit.Server.getInstance().getPluginManager().callEvent(playerCommandPreprocessEvent);
                                if (playerCommandPreprocessEvent.isCancelled()) {
                                    return;
                                }

                                Timings.playerCommandTimer.startTiming();
                                cn.nukkit.Server.getInstance().dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
                                Timings.playerCommandTimer.stopTiming();
                                return;
                            }
                            TextPacket pk = new TextPacket();
                            pk.type = TextPacket.TYPE_CHAT;
                            pk.message = packet.getMessage();
                            pk.source = notch.getName();
                            notch.handleDataPacket(pk);
                        }
                    }
                });
            }

            @Override
            public void sessionRemoved(SessionRemovedEvent event) {
                MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
                if(protocol.getSubProtocol() == SubProtocol.GAME) {
                }
            }
        });

        server.bind();
        
        while (true) {}
    }
}
