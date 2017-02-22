package net.pocketdreams.sequinland.notchian.translator.pocket;

import java.util.ArrayList;
import java.util.Arrays;

import org.spacehq.mc.protocol.data.game.chunk.Chunk;
import org.spacehq.mc.protocol.data.game.chunk.Column;
import org.spacehq.mc.protocol.data.game.entity.metadata.Position;
import org.spacehq.mc.protocol.data.game.world.block.BlockChangeRecord;
import org.spacehq.mc.protocol.data.game.world.block.BlockState;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.FullChunkDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.utils.BinaryStream;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.PocketPacketTranslator;

public class PEUpdateBlockPacketTranslator extends PocketPacketTranslator {

    @Override
    public Packet[] translate(DataPacket packet, NotchianPlayer player) {
        UpdateBlockPacket pk = (UpdateBlockPacket) packet;
        
        BlockChangeRecord record = new BlockChangeRecord(new Position(pk.x, pk.y, pk.z), new BlockState(pk.blockId, pk.blockData));
        
        ServerBlockChangePacket pcPacket = new ServerBlockChangePacket(record);
        return new Packet[] { pcPacket };
    }

}
