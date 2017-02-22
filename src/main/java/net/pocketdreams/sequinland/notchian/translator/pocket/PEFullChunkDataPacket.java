package net.pocketdreams.sequinland.notchian.translator.pocket;

import java.util.ArrayList;
import java.util.Arrays;

import org.spacehq.mc.protocol.data.game.chunk.Chunk;
import org.spacehq.mc.protocol.data.game.chunk.Column;
import org.spacehq.mc.protocol.data.game.world.block.BlockState;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.FullChunkDataPacket;
import cn.nukkit.utils.BinaryStream;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.PocketPacketTranslator;

public class PEFullChunkDataPacket extends PocketPacketTranslator {

    @Override
    public Packet[] translate(DataPacket packet, NotchianPlayer player) {
        FullChunkDataPacket pk = (FullChunkDataPacket) packet;

        byte[] biomeArray = new byte[256];
        Arrays.fill(biomeArray, (byte) 5);

        ArrayList<Chunk> chunks = new ArrayList<Chunk>();
        
        byte[] payload = pk.data;
        BinaryStream stream = new BinaryStream(payload);
        int count = stream.getByte();
        System.out.println("Section Count: " + count);
        for (int i = 0; i < count; i++) {
            stream.getByte();
            byte[] section = stream.get(10240); // wow!
            System.out.println("Chunk section loaded");
            
            Chunk chk = new Chunk(true);
            
            BinaryStream chunkStream = new BinaryStream(section);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    // Block IDs
                    int blockIdx = (x << 7) | (z << 3);
                    for (int y = 0; y < 16; y++) {
                        int id = chunkStream.getByte();
                        int meta = section[4096 + (blockIdx | (y >> 1))];
                        chk.getBlocks().set(x, y, z, new BlockState(id, meta));
                        chk.getBlockLight().set(x, y, z, 15);
                    }
                }
            }
            chunks.add(chk);
        }
        
        int idx = chunks.size();
        
        while (16 > idx) {
            chunks.add(new Chunk(true));
            idx++;
        }

        System.out.println("Chunk count: " + chunks.size());
        Column col = new Column(pk.chunkX, pk.chunkZ, chunks.toArray(new Chunk[0]), biomeArray, null);
        System.out.println(pk.chunkX + ", " + pk.chunkZ + ", " + pk.data.length);
        ServerChunkDataPacket pcPacket = new ServerChunkDataPacket(col);
        return new Packet[] { pcPacket };
    }

}
