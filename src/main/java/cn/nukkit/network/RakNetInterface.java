package cn.nukkit.network;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.raknet.protocol.EncapsulatedPacket;
import cn.nukkit.raknet.protocol.packet.PING_DataPacket;
import cn.nukkit.raknet.server.ServerInstance;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.MainLogger;
import net.marfgamer.jraknet.RakNetPacket;
import net.marfgamer.jraknet.identifier.MCPEIdentifier;
import net.marfgamer.jraknet.protocol.Reliability;
import net.marfgamer.jraknet.server.RakNetServer;
import net.marfgamer.jraknet.server.RakNetServerListener;
import net.marfgamer.jraknet.session.RakNetClientSession;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RakNetInterface implements ServerInstance, AdvancedSourceInterface {

    private final Server server;

    private Network network;

    private final RakNetServer raknet;

    private final Map<String, Player> players = new ConcurrentHashMap<>();

    private final Map<String, Integer> networkLatency = new ConcurrentHashMap<>();

    private final Map<Integer, String> identifiers = new ConcurrentHashMap<>();

    private final Map<String, Integer> identifiersACK = new ConcurrentHashMap<>();

    private int[] channelCounts = new int[256];

    public RakNetInterface(Server server) {
        this.server = server;

        raknet = new RakNetServer(server.getPort(), server.getMaxPlayers(), new MCPEIdentifier(server.getMotd(), ProtocolInfo.CURRENT_PROTOCOL, ProtocolInfo.MINECRAFT_VERSION_NETWORK, server.getOnlinePlayers().size(),
                server.getMaxPlayers(), System.currentTimeMillis(), "New World", "Survival"));
        raknet.setListener(new RakNetServerListener() {
            // Client connected
            @Override
            public void onClientConnect(RakNetClientSession session) {
                openSession(String.valueOf(session.getGloballyUniqueId()), session.getAddress().getHostString(), session.getInetPort(), session.getGloballyUniqueId());
            }

            // Client disconnected
            @Override
            public void onClientDisconnect(RakNetClientSession session, String reason) {
            }

            // Packet received
            @Override
            public void handlePacket(RakNetClientSession session, RakNetPacket packet, int channel) {
                handleRakNetPacket(session.getGloballyUniqueId(), packet);
            }
        });

        // Start server
        raknet.startThreaded();
    }

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public boolean process() {
        boolean work = false;
        return work;
    }

    @Override
    public void closeSession(String identifier, String reason) {
        if (this.players.containsKey(identifier)) {
            Player player = this.players.get(identifier);
            this.identifiers.remove(player.rawHashCode());
            this.players.remove(identifier);
            this.networkLatency.remove(identifier);
            this.identifiersACK.remove(identifier);
            player.close(player.getLeaveMessage(), reason);
        }
    }

    @Override
    public int getNetworkLatency(Player player) {
        return this.networkLatency.get(this.identifiers.get(player.rawHashCode()));
    }

    @Override
    public void close(Player player) {
        this.close(player, "unknown reason");
    }

    @Override
    public void close(Player player, String reason) {
        if (this.identifiers.containsKey(player.rawHashCode())) {
            String id = this.identifiers.get(player.rawHashCode());
            this.players.remove(id);
            this.networkLatency.remove(id);
            this.identifiersACK.remove(id);
            this.closeSession(id, reason);
            this.identifiers.remove(player.rawHashCode());
        }
    }

    @Override
    public void shutdown() {
        this.raknet.shutdown();
    }

    @Override
    public void emergencyShutdown() {
        // TODO: What is a "emergency shutdown"?
        this.raknet.shutdown();
    }

    @Override
    public void openSession(String identifier, String address, int port, long clientID) {
        PlayerCreationEvent ev = new PlayerCreationEvent(this, Player.class, Player.class, null, address, port);
        this.server.getPluginManager().callEvent(ev);
        Class<? extends Player> clazz = ev.getPlayerClass();

        try {
            Constructor constructor = clazz.getConstructor(SourceInterface.class, Long.class, String.class, int.class);
            Player player = (Player) constructor.newInstance(this, ev.getClientId(), ev.getAddress(), ev.getPort());
            player.globalRakNetId = clientID;
            this.players.put(identifier, player);
            this.networkLatency.put(identifier, 0);
            this.identifiersACK.put(identifier, 0);
            this.identifiers.put(player.rawHashCode(), identifier);
            this.server.addPlayer(identifier, player);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Server.getInstance().getLogger().logException(e);
        }
    }

    public void handleRakNetPacket(long identifier, RakNetPacket packet) {
        if (this.players.containsKey(String.valueOf(identifier))) {
            DataPacket pk = null;
            try {
                if (packet.array().length > 0) {
                    if (packet.array()[0] == PING_DataPacket.ID) {
                        PING_DataPacket pingPacket = new PING_DataPacket();
                        pingPacket.buffer = packet.array();
                        pingPacket.decode();

                        this.networkLatency.put(String.valueOf(identifier), (int) pingPacket.pingID);
                        return;
                    }

                    pk = this.getPacket(packet.array());
                    if (pk != null) {
                        pk.decode();
                        this.players.get(String.valueOf(identifier)).handleDataPacket(pk);
                    }
                }
            } catch (Exception e) {
                this.server.getLogger().logException(e);
                if (Nukkit.DEBUG > 1 && pk != null) {
                    MainLogger logger = this.server.getLogger();
                    //                    if (logger != null) {
                    logger.debug("Packet " + pk.getClass().getName() + " 0x" + Binary.bytesToHexString(packet.array()));
                    //logger.logException(e);
                    //                    }
                }

                if (this.players.containsKey(String.valueOf(identifier))) {
                    try {
                        this.raknet.blockAddress(InetAddress.getByName(this.players.get(identifier).getAddress()), 5);
                    } catch (UnknownHostException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
    
    @Override
    public void handleEncapsulated(String identifier, EncapsulatedPacket packet, int flags) {
        throw new RuntimeException();
    }

    @Override
    public void blockAddress(String address) {
        this.blockAddress(address, 300);
    }

    @Override
    public void blockAddress(String address, int timeout) {
        try {
            this.raknet.blockAddress(InetAddress.getByName(address), timeout);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleRaw(String address, int port, byte[] payload) {
        this.server.handlePacket(address, port, payload);
    }

    @Override
    public void sendRawPacket(String address, int port, byte[] payload) {
        // TODO: Fix this! If this isn't fixed, query WON'T work!
    }

    @Override
    public void notifyACK(String identifier, int identifierACK) {
        // TODO: Better ACK notification implementation!
        for (Player p : server.getOnlinePlayers().values()) {
            p.notifyACK(identifierACK);
        }
    }

    @Override
    public void setName(String name) {
        QueryRegenerateEvent info = this.server.getQueryInformation();

        raknet.setIdentifier(new MCPEIdentifier(server.getMotd(), ProtocolInfo.CURRENT_PROTOCOL, ProtocolInfo.MINECRAFT_VERSION_NETWORK, info.getPlayerCount(),
                info.getMaxPlayerCount(), System.currentTimeMillis(), "New World", "Survival"));
    }

    public void setPortCheck(boolean value) {
        // TODO: What is this?
        // this.handler.sendOption("portChecking", String.valueOf(value));
    }

    @Override
    public void handleOption(String name, String value) {
        if ("bandwidth".equals(name)) {
            String[] v = value.split(";");
            this.network.addStatistics(Double.valueOf(v[0]), Double.valueOf(v[1]));
        }
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet) {
        return this.putPacket(player, packet, false);
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet, boolean needACK) {
        return this.putPacket(player, packet, needACK, false);
    }

    @Override
    public Integer putPacket(Player player, DataPacket packet, boolean needACK, boolean immediate) {
        if (this.identifiers.containsKey(player.rawHashCode())) {
            if (!packet.isEncoded) {
                packet.encode();
            }
            RakNetClientSession session = raknet.getSession(player.globalRakNetId);
            RakNetPacket wrapper  = new RakNetPacket(0xFE); // RakNetPacket will automatically write the ID byte you give to it
            wrapper.write(packet.getBuffer());
            session.sendMessage(Reliability.RELIABLE_ORDERED, wrapper);

            // TODO: Fix ACK
            return 0;
        }

        return null;

    }

    private DataPacket getPacket(byte[] buffer) {
        byte pid = buffer[0];
        int start = 1;

        if (pid == (byte) 0xfe) {
            pid = buffer[1];
            start++;
        }
        DataPacket data = this.network.getPacket(pid);

        if (data == null) {
            return null;
        }

        data.setBuffer(buffer, start);

        return data;
    }
}