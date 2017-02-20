package net.pocketdreams.sequinland.network.protocol;

import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;

public class RequestChunkRadiusPacket extends GamePacket {
    public int radius;
    
    public RequestChunkRadiusPacket() {
        super();
    }

    public RequestChunkRadiusPacket(Packet packet) {
        super(packet);
    }
    
    @Override
    public void decode() {
        this.radius = this.readSignedVarInt();
    }
    
    @Override
    public byte pid() {
        return ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;
    }
}
