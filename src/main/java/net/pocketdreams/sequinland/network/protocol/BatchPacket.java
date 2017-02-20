package net.pocketdreams.sequinland.network.protocol;

import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;

public class BatchPacket extends GamePacket {
    public byte[] payload;
    
    public BatchPacket() {
        super();
    }

    public BatchPacket(Packet packet) {
        super(packet);
    }
    
    @Override
    public void decode() {
        this.payload = this.readByteArray();
    }
    
    @Override
    public void encode() {
        this.writeByte(pid());
        this.writeByteArray(this.payload);
    }
    
    @Override
    public byte pid() {
        return ProtocolInfo.BATCH_PACKET;
    }
}
