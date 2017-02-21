package net.pocketdreams.sequinland.notchian.client;

import org.spacehq.mc.protocol.data.game.entity.player.GameMode;
import org.spacehq.mc.protocol.data.game.setting.Difficulty;
import org.spacehq.mc.protocol.data.game.world.WorldType;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.packetlib.Session;

import cn.nukkit.Player;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.network.protocol.TextPacket;

public class NotchianClient extends Player {
    Session session;
    
    public NotchianClient(Session session, Long clientID, String ip, int port) {
        super(null, clientID, ip, port);
        this.session = session;
    }

    @Override
    public void handleDataPacket(DataPacket packet) {
        System.out.println("Processing " + packet.getClass().getSimpleName() + "...");
        super.handleDataPacket(packet);
        return;
    }
    
    @Override
    public int dataPacket(DataPacket packet, boolean needACK) {
        System.out.println("Sending " + packet.getClass().getSimpleName());
        if (packet instanceof StartGamePacket) {
            StartGamePacket pk = (StartGamePacket) packet;
            session.send(new ServerJoinGamePacket(0, false, GameMode.values()[pk.gamemode], pk.dimension, Difficulty.values()[pk.difficulty], 10, WorldType.DEFAULT, false));
            
            session.send(new ServerPlayerPositionRotationPacket(pk.x, pk.y, pk.z, 0, 0, 0));
            
            return -1;
        }
        if (packet instanceof TextPacket) {
            ServerChatPacket pk = new ServerChatPacket(((TextPacket) packet).message);
            session.send(pk);
            return -1;
        }
        return -1;
    }
    
    @Override
    public int directDataPacket(DataPacket packet, boolean needACK) {
        System.out.println("Sending directly " + packet.getClass().getSimpleName());
        return -1;
    }
    
    @Override
    public void close(TextContainer message, String reason, boolean notify) {
        System.out.println("Closing connection due to " + reason);
    }
}
