package net.pocketdreams.sequinland.notchian.translator.pocket;

import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.PocketPacketTranslator;

public class PEMovePlayerPacketTranslator extends PocketPacketTranslator {

    @Override
    public Packet[] translate(DataPacket packet, NotchianPlayer player) {
        MovePlayerPacket pePacket = (MovePlayerPacket) packet;

        ServerEntityPositionRotationPacket pcPacket = new ServerEntityPositionRotationPacket((int) pePacket.eid, pePacket.x, pePacket.y, pePacket.z, pePacket.yaw, pePacket.pitch, pePacket.onGround);
        
        // Broken, the client gets telepoted instead of the entity... TODO: Fix
        return new Packet[] {};
    }

}
