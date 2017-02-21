package net.pocketdreams.sequinland.notchian.client;

import java.util.ArrayList;
import java.util.List;

import org.spacehq.mc.protocol.data.game.chunk.Chunk;
import org.spacehq.mc.protocol.data.game.chunk.Column;
import org.spacehq.mc.protocol.data.game.entity.player.GameMode;
import org.spacehq.mc.protocol.data.game.setting.Difficulty;
import org.spacehq.mc.protocol.data.game.world.WorldType;
import org.spacehq.mc.protocol.data.game.world.block.BlockState;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import org.spacehq.opennbt.tag.builtin.CompoundTag;
import org.spacehq.packetlib.Session;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.FullChunkDataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Zlib;

public class NotchianClient extends Player {
    Session session;
    
    public NotchianClient(Session session, Long clientID, String ip, int port) {
        super(null, clientID, ip, port);
        this.session = session;
    }

    @Override
    public void handleDataPacket(DataPacket packet) {
        System.out.println("Processing " + packet.getClass().getSimpleName() + "...");
        super.handleDataPacket(packet);
        return;
    }
    
    @Override
    public int dataPacket(DataPacket packet, boolean needACK) {
        
        if (packet instanceof BatchPacket) {
            // Decode BatchPackets
            List<DataPacket> packets = decodeBatchPacket((BatchPacket) packet);
            
            for (DataPacket pk : packets) {
                this.dataPacket(pk); // Parse it again
            }
            return -1;
        } else {
            System.out.println("Sending " + packet.getClass().getSimpleName());
        }
        if (packet instanceof StartGamePacket) {
            StartGamePacket pk = (StartGamePacket) packet;
            session.send(new ServerJoinGamePacket(0, false, GameMode.values()[pk.gamemode], pk.dimension, Difficulty.values()[pk.difficulty], 10, WorldType.DEFAULT, false));
            
            session.send(new ServerPlayerPositionRotationPacket(pk.x, pk.y, pk.z, 0, 0, 0));
            
            // Request chunk data
            RequestChunkRadiusPacket pocketPk = new RequestChunkRadiusPacket();
            pocketPk.radius = 5;
            this.handleDataPacket(pocketPk);
            return -1;
        }
        if (packet instanceof TextPacket) {
            ServerChatPacket pk = new ServerChatPacket(((TextPacket) packet).message);
            session.send(pk);
            return -1;
        }
        if (packet instanceof FullChunkDataPacket) {
            FullChunkDataPacket pocketPk = (FullChunkDataPacket) packet;
            
            Chunk chk = new Chunk(true);
            for (int x = 0; 15 > x; x++) {
                for (int y = 0; 15 > y; y++) {
                    for (int z = 0; 15 > z; z++) {
                        chk.getBlocks().set(x, y, z, new BlockState(1, 0)); // I love stone
                    }
                }
            }
            Column col = new Column(pocketPk.chunkX, pocketPk.chunkZ, new Chunk[] { chk , chk , chk, chk, chk, chk, chk, chk, chk , chk , chk, chk, chk, chk, chk, chk }, new CompoundTag[0]);
            ServerChunkDataPacket pk = new ServerChunkDataPacket(col);
            session.send(pk);
            return -1;
        }
        return -1;
    }
    
    @Override
    public int directDataPacket(DataPacket packet, boolean needACK) {
        System.out.println("Sending directly " + packet.getClass().getSimpleName());
        return -1;
    }
    
    @Override
    public void close(TextContainer message, String reason, boolean notify) {
        System.out.println("Closing connection due to " + reason);
    }
    
    public static List<DataPacket> decodeBatchPacket(BatchPacket packet) {
        byte[] data;
        try {
            data = Zlib.inflate(packet.payload, 64 * 1024 * 1024);
        } catch (Exception e) {
            return null;
        }

        int len = data.length;
        BinaryStream stream = new BinaryStream(data);
        try {
            List<DataPacket> packets = new ArrayList<>();
            while (stream.offset < len) {
                byte[] buf = stream.getByteArray();

                DataPacket pk;

                if ((pk = Server.getInstance().getNetwork().getPacket(buf[0])) != null) {
                    if (pk.pid() == ProtocolInfo.BATCH_PACKET) {
                        throw new IllegalStateException("Invalid BatchPacket inside BatchPacket");
                    }

                    pk.setBuffer(buf, 1);

                    pk.decode();

                    packets.add(pk);
                }
            }

            return packets;
        } catch (Exception e) {
            if (Nukkit.DEBUG > 0) {
                Server.getInstance().getLogger().debug("BatchPacket 0x" + Binary.bytesToHexString(packet.payload));
                // Server.getInstance().logException(e);
            }
        }
        return null;
    }
}
