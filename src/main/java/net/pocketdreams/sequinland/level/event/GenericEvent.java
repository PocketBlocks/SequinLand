package net.pocketdreams.sequinland.level.event;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

public class GenericEvent extends Vector3 {
    public int evid;
    public int data;
    public int pitch = 1;
    
    public GenericEvent(double x, double y, double z) {
        super(x, y, z);
    }
    
    public GenericEvent(Vector3 vec) {
        super(vec.x, vec.y, vec.z);
    }
    
    public DataPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = evid;
        pk.data = data;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;

        return new DataPacket[]{pk};
    }
}
