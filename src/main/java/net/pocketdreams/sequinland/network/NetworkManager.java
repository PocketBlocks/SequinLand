package net.pocketdreams.sequinland.network;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerCreationEvent;
import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;
import net.marfgamer.jraknet.RakNetPacket;
import net.marfgamer.jraknet.identifier.MCPEIdentifier;
import net.marfgamer.jraknet.server.RakNetServer;
import net.marfgamer.jraknet.session.RakNetClientSession;
import net.marfgamer.jraknet.util.map.IntMap;
import net.pocketdreams.sequinland.network.protocol.GamePacket;
import net.pocketdreams.sequinland.network.protocol.LoginPacket;

public class NetworkManager extends RakNetServer {
    private final Server server;
    private final IntMap<Class<? extends GamePacket>> packets;
    private final HashMap<Long, Player> players = new HashMap<Long, Player>();
    
    public NetworkManager(Server server) {
        super(server.getPort(), server.getMaxPlayers(),
                new MCPEIdentifier(server.getMotd(), ProtocolInfo.CURRENT_PROTOCOL, ProtocolInfo.MINECRAFT_VERSION_NETWORK, 0,
                        server.getMaxPlayers(), System.currentTimeMillis(), "survival", "survival"));
        this.server = server;

        this.packets = new IntMap<Class<? extends GamePacket>>();
        packets.put(ProtocolInfo.LOGIN_PACKET, LoginPacket.class);
    }

    @Override
    public void onClientConnect(RakNetClientSession session) {
        PlayerCreationEvent ev = new PlayerCreationEvent(Player.class, Player.class, null, session.getAddress().getHostString(), session.getAddress().getPort());
        this.server.getPluginManager().callEvent(ev);
        Class<? extends Player> clazz = ev.getPlayerClass();

        try {
            Constructor constructor = clazz.getConstructor(RakNetClientSession.class, Long.class, String.class, int.class);
            Player player = (Player) constructor.newInstance(session, ev.getClientId(), ev.getAddress(), ev.getPort());
            this.players.put(session.getGloballyUniqueId(), player);
            /* this.networkLatency.put(identifier, 0);
            this.identifiersACK.put(identifier, 0);
            this.identifiers.put(player.rawHashCode(), identifier); */
            this.server.addPlayer(String.valueOf(session.getGloballyUniqueId()), player);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Server.getInstance().getLogger().logException(e);
        }
    }

    @Override
    public void onClientDisconnect(RakNetClientSession session, String reason) {
        Player player = players.get(session.getGloballyUniqueId());
        if (player == null) {
            throw new RuntimeException("Trying to handle disconnect for a unknown player! Global ID: " + session.getGloballyUniqueId());
        }
        player.close(reason);
        players.remove(session.getGloballyUniqueId());
    }
    
    @Override
    public void handlePacket(RakNetClientSession session, RakNetPacket raknetPacket, int channel) {
        if (raknetPacket.getId() != 0xFE) {
            System.out.println("Invalid MCPE packet, not 0xFE");
            return; // It isn't an MCPE packet
        }
        // Decode packet id
        byte id = raknetPacket.readByte();
        Player player = players.get(session.getGloballyUniqueId());
        if (player == null) {
            throw new RuntimeException("Trying to handle a packet for a unknown player! Global ID: " + session.getGloballyUniqueId());
        }
        try {
            Class<? extends GamePacket> clz = this.packets.get(id);
            if (clz == null) {
                System.out.println("No decoder for " + id + "!");
                return;
            }
            GamePacket pk = clz.getDeclaredConstructor(Packet.class).newInstance(raknetPacket);
            pk.decode(); // Pre decode
            player.handleGamePacket(pk);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
