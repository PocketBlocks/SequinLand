package net.pocketdreams.sequinland.notchian.translator.notch;

import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.NotchPacketTranslator;

public class PCClientPlayerPlaceBlockPacketTranslator extends NotchPacketTranslator {

    @Override
    public DataPacket[] translate(Packet packet, NotchianPlayer player) {
        // ClientPlayerPlaceBlockPacket pcPacket = (ClientPlayerPlaceBlockPacket) packet;
        // MovePlayerPacket pePacket = new MovePlayerPacket();
        return new DataPacket[] {};
    }
}
