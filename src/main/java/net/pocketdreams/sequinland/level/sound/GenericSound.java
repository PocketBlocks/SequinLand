package net.pocketdreams.sequinland.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class GenericSound extends Vector3 {
    public byte type;
    public int volume = -1;
    public int pitch = 1;
    
    public GenericSound(double x, double y, double z) {
        super(x, y, z);
    }
    
    public GenericSound(Vector3 vec) {
        super(vec.x, vec.y, vec.z);
    }
    
    public DataPacket[] encode() {
        LevelSoundEventPacket pk = new LevelSoundEventPacket();
        pk.type = type;
        pk.volume = volume;
        pk.pitch = pitch;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;

        return new DataPacket[]{pk};
    }
}
