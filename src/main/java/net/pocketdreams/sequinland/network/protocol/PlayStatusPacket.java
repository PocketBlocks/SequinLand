package net.pocketdreams.sequinland.network.protocol;

import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;

public class PlayStatusPacket extends GamePacket {
    public long status;
    
    public static final long OK = 0;
    public static final long OUTDATED_CLIENT = 1;
    public static final long OUTDATED_SERVER = 2;
    public static final long SPAWNED = 3;
    public static final long INVALID_TENANT = 4;
    public static final long EDITION_MISMATCH = 5;
    
    public PlayStatusPacket() {
        super();
    }

    public PlayStatusPacket(Packet packet) {
        super(packet);
    }
    
    @Override
    public void encode() {
        this.writeByte(pid());
        this.writeUInt(status);
    }
    
    @Override
    public byte pid() {
        return ProtocolInfo.PLAY_STATUS_PACKET;
    }
}
