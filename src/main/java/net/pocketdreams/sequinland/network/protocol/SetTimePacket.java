package net.pocketdreams.sequinland.network.protocol;

import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;

public class SetTimePacket extends GamePacket {
    public int time;
    public boolean daylightCycle;
    
    public SetTimePacket() {
        super();
    }

    public SetTimePacket(Packet packet) {
        super(packet);
    }
    
    @Override
    public void encode() {
        this.writeByte(pid());
        this.writeSignedVarInt(time);
        this.writeBoolean(daylightCycle);
    }
    
    @Override
    public byte pid() {
        return ProtocolInfo.SET_TIME_PACKET;
    }
}
