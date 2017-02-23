package net.pocketdreams.sequinland.notchian.translator.pocket;

import org.spacehq.mc.protocol.data.game.entity.metadata.EntityMetadata;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.DataPacket;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.PocketPacketTranslator;

public class PEAddPlayerPacketTranslator extends PocketPacketTranslator {

    @Override
    public Packet[] translate(DataPacket packet, NotchianPlayer player) {
        AddPlayerPacket pk = (AddPlayerPacket) packet;

        ServerSpawnPlayerPacket pcPacket = new ServerSpawnPlayerPacket((int) pk.entityUniqueId, pk.uuid, pk.x, pk.y, pk.z, pk.yaw, pk.pitch, new EntityMetadata[] {});
        
        return new Packet[] { pcPacket };
    }

}
