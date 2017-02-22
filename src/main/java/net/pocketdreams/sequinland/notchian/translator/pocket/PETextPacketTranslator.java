package net.pocketdreams.sequinland.notchian.translator.pocket;

import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.TextPacket;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.PocketPacketTranslator;

public class PETextPacketTranslator extends PocketPacketTranslator {

    @Override
    public Packet[] translate(DataPacket packet, NotchianPlayer player) {
        return new Packet[] { new ServerChatPacket(((TextPacket) packet).message) };
    }

}
