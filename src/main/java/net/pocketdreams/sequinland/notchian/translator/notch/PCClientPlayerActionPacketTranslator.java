package net.pocketdreams.sequinland.notchian.translator.notch;

import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerActionPacket;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.NotchPacketTranslator;

public class PCClientPlayerActionPacketTranslator extends NotchPacketTranslator {

    @Override
    public DataPacket[] translate(Packet packet, NotchianPlayer player) {
        ClientPlayerActionPacket pcPacket = (ClientPlayerActionPacket) packet;
        int action = -1;
        switch (pcPacket.getAction()) {
        case CANCEL_DIGGING:
            action = 1;
            break;
        case DROP_ITEM:
            action = 5; // TODO: I think this is wrong
            break;
        case DROP_ITEM_STACK:
            action = 5; // TODO: I think this is wrong
            break;
        case FINISH_DIGGING:
            action = 2;
            break;
        case RELEASE_USE_ITEM:
            action = 5;
            break;
        case START_DIGGING:
            action = 0;
            break;
        default:
            break;
        }
        if (action == -1) { return new DataPacket[] {}; }
        PlayerActionPacket pePacket = new PlayerActionPacket();
        pePacket.entityId = 0;
        pePacket.x = pcPacket.getPosition().getX();
        pePacket.y = pcPacket.getPosition().getY();
        pePacket.z = pcPacket.getPosition().getZ();
        pePacket.face = pcPacket.getFace().ordinal(); // TODO: Proper conversion
        pePacket.action = action;
        return new DataPacket[] { pePacket };
    }
}
