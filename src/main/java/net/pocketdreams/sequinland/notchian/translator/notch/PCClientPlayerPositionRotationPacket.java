package net.pocketdreams.sequinland.notchian.translator.notch;

import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.NotchPacketTranslator;

public class PCClientPlayerPositionRotationPacket extends NotchPacketTranslator {

    @Override
    public DataPacket[] translate(Packet packet, NotchianPlayer player) {
        ClientPlayerPositionRotationPacket pcPacket = (ClientPlayerPositionRotationPacket) packet;
        MovePlayerPacket pePacket = new MovePlayerPacket();
        pePacket.x = (float) pcPacket.getX();
        pePacket.y = (float) (pcPacket.getY() + 1.62F);
        pePacket.z = (float) pcPacket.getZ();
        pePacket.eid = 0;
        pePacket.headYaw = (float) pcPacket.getYaw();
        pePacket.onGround = pcPacket.isOnGround();
        pePacket.yaw = (float) pcPacket.getYaw();
        pePacket.pitch = (float) pcPacket.getPitch();
        return new DataPacket[] { pePacket };
    }

}
