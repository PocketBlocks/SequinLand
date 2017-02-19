package net.pocketdreams.sequinland.network.protocol;

import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;

public class SetCommandsEnabledPacket extends GamePacket {
    public boolean enabled;
    
    public SetCommandsEnabledPacket() {
        super();
    }

    public SetCommandsEnabledPacket(Packet packet) {
        super(packet);
    }
    
    @Override
    public void encode() {
        this.writeByte(pid());
        this.writeBoolean(enabled);
    }
    
    @Override
    public byte pid() {
        return ProtocolInfo.SET_COMMANDS_ENABLED_PACKET;
    }
}
