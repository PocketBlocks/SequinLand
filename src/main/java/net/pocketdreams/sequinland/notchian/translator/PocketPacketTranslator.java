package net.pocketdreams.sequinland.notchian.translator;

import java.util.HashMap;

import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.FullChunkDataPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.pocket.PEFullChunkDataPacket;
import net.pocketdreams.sequinland.notchian.translator.pocket.PEServerJoinGamePacketTranslator;
import net.pocketdreams.sequinland.notchian.translator.pocket.PETextPacketTranslator;
import net.pocketdreams.sequinland.notchian.translator.pocket.PEUpdateBlockPacketTranslator;

//Translate Pocket packets to PC packets
public abstract class PocketPacketTranslator {
    public static HashMap<Class<? extends DataPacket>, Class<? extends PocketPacketTranslator>> translators = new HashMap<>();

    public abstract Packet[] translate(DataPacket packet, NotchianPlayer player);

    static {
        translators.put(StartGamePacket.class, PEServerJoinGamePacketTranslator.class);
        translators.put(TextPacket.class, PETextPacketTranslator.class);
        translators.put(FullChunkDataPacket.class, PEFullChunkDataPacket.class);
        translators.put(UpdateBlockPacket.class, PEUpdateBlockPacketTranslator.class);
    }
}
