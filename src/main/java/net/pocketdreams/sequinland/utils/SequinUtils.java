package net.pocketdreams.sequinland.utils;

import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.network.protocol.UpdateBlockPacket;

public class SequinUtils {
    private static List<Integer> from = Arrays.asList(1);
    private static List<Integer> to = Arrays.asList(56, 129, 15, 16, 14, 21, 73);
    private static SplittableRandom random = new SplittableRandom();
    
    public static boolean shouldReplace(int id) {
        return from.contains(id);
    }
    
    public static int getRandomToId() {
        return to.get(random.nextInt(to.size()));
    }
    
    // TODO: Something more pretty
    public static void init() {
        if (Server.getInstance().getSequinLandConfig().exists("anti-xray.change-from")) {
            from = Server.getInstance().getSequinLandConfig().getIntegerList("anti-xray.change-from");
        }
        if (Server.getInstance().getSequinLandConfig().exists("anti-xray.change-to")) {
            to = Server.getInstance().getSequinLandConfig().getIntegerList("anti-xray.change-to");
        }
    }
    
    /**
     * Checks if there is any transparents blocks nearby
     * @param cs
     * @param x
     * @param y
     * @param z
     * @param radius
     * @return
     */
    public static boolean hasTransparentBlockAdjacent(ChunkSection cs, int x, int y, int z, int radius)
    {
        return !isSolidBlock(cs, x, y, z) /* isSolidBlock */
                || ( radius > 0
                        && ( hasTransparentBlockAdjacent( cs, x + 1, y, z, radius - 1 )
                                || hasTransparentBlockAdjacent( cs, x, y, z + 1, radius - 1 )
                                || hasTransparentBlockAdjacent( cs, x, y + 1, z, radius - 1 )
                                || hasTransparentBlockAdjacent( cs, x, y - 1, z, radius - 1 )
                                || hasTransparentBlockAdjacent( cs, x - 1, y, z, radius - 1 )
                                || hasTransparentBlockAdjacent( cs, x, y, z - 1, radius - 1 ) ) );
    }

    /**
     * Check if the current block is a solid block
     * @param cs
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static boolean isSolidBlock(ChunkSection cs, int x, int y, int z) {
        try {
            return !Block.transparent[cs.getBlockId(x, y, z)];
        } catch (Exception e) {
            return true; // Sometimes it will throw an exception when checking blocks outside the current ChunkSection, so let's just silently ignore it
        }
    }

    /**
     * Send a nearby block update to everyone
     * 
     * @param x
     * @param y
     * @param z
     */
    public static void updateNearbyBlocks(Level lvl, int x, int y, int z, int radius) {
        UpdateBlockPacket pk = new UpdateBlockPacket();
        int blockId = lvl.getBlockIdAt(x, y, z);
        int blockData = lvl.getBlockDataAt(x, y, z);
        pk.blockId = blockId;
        pk.blockData = blockData;
        pk.x = x;
        pk.y = y;
        pk.z = z;

        lvl.getPlayers().values().forEach((player) -> player.dataPacket(pk));
        
        if (radius > 0) {
            updateNearbyBlocks( lvl, x + 1, y, z, radius - 1 );
            updateNearbyBlocks( lvl, x, y, z + 1, radius - 1 );
            updateNearbyBlocks( lvl, x, y + 1, z, radius - 1 );
            updateNearbyBlocks( lvl, x, y - 1, z, radius - 1 );
            updateNearbyBlocks( lvl, x - 1, y, z, radius - 1 );
            updateNearbyBlocks( lvl, x, y, z - 1, radius - 1 );
        }
    }
}
