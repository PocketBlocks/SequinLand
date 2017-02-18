package net.pocketdreams.sequinland.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockPlaceSound extends GenericSound {
    public BlockPlaceSound(double x, double y, double z) {
        super(x, y, z);
        this.type = LevelSoundEventPacket.SOUND_PLACE;
        this.volume = 100; // If it isn't 100, the player may not hear the sound
    }
    
    public BlockPlaceSound(Vector3 vec) {
        this(vec.x, vec.y, vec.z);
    }
}
