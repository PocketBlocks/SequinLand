package net.pocketdreams.sequinland.network.protocol;

import java.util.UUID;

import cn.nukkit.network.protocol.ProtocolInfo;
import net.marfgamer.jraknet.Packet;

public class LoginPacket extends GamePacket {
    public long protocolVersion;
    public short gameEdition;
    public byte[] payload;
    public String username = "Unknown";
    public int deviceOperatingSystem = 0;
    public long clientId = -1;
    public UUID clientUUID = UUID.randomUUID();
    public String deviceModel = "Computer";
    
    public LoginPacket() {
        super();
    }

    public LoginPacket(Packet packet) {
        super(packet);
    }
    
    @Override
    public void decode() {
        this.protocolVersion = this.readUInt();
        this.gameEdition = this.readUByte();
        this.payload = this.readByteArray();
        
        System.out.println("Protocol Version: " + protocolVersion);
    }
    
    @Override
    public byte pid() {
        return ProtocolInfo.LOGIN_PACKET;
    }
    
    public long getProtocol() {
        return protocolVersion;
    }
}
