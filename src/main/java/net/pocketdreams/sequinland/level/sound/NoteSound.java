package net.pocketdreams.sequinland.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class NoteSound extends GenericSound {
    public NoteSound(double x, double y, double z, int pitch) {
        super(x, y, z);
        this.type = LevelSoundEventPacket.SOUND_NOTE;
        this.pitch = pitch;
    }
    
    public NoteSound(Vector3 vec, int pitch) {
        this(vec.x, vec.y, vec.z, pitch);
    }
}
