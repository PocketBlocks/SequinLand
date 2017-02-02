package cn.nukkit.network.protocol;

public class MapInfoRequestPacket extends DataPacket {
    public long mapId;
    
    @Override
    public byte pid() {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        mapId = this.getVarLong();
    }

    @Override
    public void encode() {

    }
}
