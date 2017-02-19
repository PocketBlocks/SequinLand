package net.pocketdreams.sequinland.network.protocol;

import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;

public class AvailableCommandsPacket extends GamePacket {
    public String commands; // JSON-encoded command data
    public String unknown = "";
    
    public AvailableCommandsPacket() {
        super();
    }

    public AvailableCommandsPacket(Packet packet) {
        super(packet);
    }
    
    @Override
    public void encode() {
        this.writeByte(pid());
        this.writeVarString(commands);
        this.writeVarString(unknown);
    }
    
    @Override
    public byte pid() {
        return ProtocolInfo.AVAILABLE_COMMANDS_PACKET;
    }
}
