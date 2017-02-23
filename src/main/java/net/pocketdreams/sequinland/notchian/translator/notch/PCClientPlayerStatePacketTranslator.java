package net.pocketdreams.sequinland.notchian.translator.notch;

import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerActionPacket;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.NotchPacketTranslator;

public class PCClientPlayerStatePacketTranslator extends NotchPacketTranslator {

    @Override
    public DataPacket[] translate(Packet packet, NotchianPlayer player) {
        ClientPlayerStatePacket pcPacket = (ClientPlayerStatePacket) packet;
        int action = -1;
        switch (pcPacket.getState()) {
        case RIDING_JUMP:
            action = 8;
            break;
        case START_SNEAKING:
            action = 11;
            break;
        case START_SPRINTING:
            action = 9;
            break;
        case STOP_SNEAKING:
            action = 12;
            break;
        case STOP_SPRINTING:
            action = 10;
            break;
        default:
            break;
        }
        if (action == -1) { return new DataPacket[] {}; }
        PlayerActionPacket pePacket = new PlayerActionPacket();
        pePacket.entityId = 0;
        pePacket.x = (int) player.getX();
        pePacket.y = (int) player.getY();
        pePacket.z = (int) player.getZ();
        pePacket.action = action;
        return new DataPacket[] { pePacket };
    }
}
