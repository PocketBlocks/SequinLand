package net.pocketdreams.sequinland.network.protocol;

import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;

public class ResourcePacksInfoPacket extends GamePacket {
    public boolean mustAccept;
    
    public ResourcePacksInfoPacket() {
        super();
    }

    public ResourcePacksInfoPacket(Packet packet) {
        super(packet);
    }
    
    @Override
    public void encode() {
        this.writeByte(pid());
        this.writeBoolean(mustAccept);
        this.writeShort(0); // No resource packs yet
        this.writeShort(0);
    }
    
    @Override
    public byte pid() {
        return ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;
    }
}
