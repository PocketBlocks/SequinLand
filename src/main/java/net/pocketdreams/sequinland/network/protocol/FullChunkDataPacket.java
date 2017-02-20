package net.pocketdreams.sequinland.network.protocol;

import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;

public class FullChunkDataPacket extends GamePacket {
    public int chunkX;
    public int chunkZ;
    public byte[] data;
    
    public FullChunkDataPacket() {
        super();
    }

    public FullChunkDataPacket(Packet packet) {
        super(packet);
    }
    
    @Override
    public void encode() {
        this.writeByte(pid());
        this.writeSignedVarInt(chunkX);
        this.writeSignedVarInt(chunkZ);
        this.writeByteArray(data);
    }
    
    @Override
    public byte pid() {
        return ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;
    }
}
