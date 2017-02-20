package net.pocketdreams.sequinland.network.protocol;

import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;

public class ChunkRadiusUpdatedPacket extends GamePacket {
    public int radius;
    
    public ChunkRadiusUpdatedPacket() {
        super();
    }

    public ChunkRadiusUpdatedPacket(Packet packet) {
        super(packet);
    }
    
    @Override
    public void encode() {
        this.writeByte(pid());
        this.writeSignedVarInt(radius);
    }
    
    @Override
    public byte pid() {
        return ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;
    }
}
