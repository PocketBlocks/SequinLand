package net.pocketdreams.sequinland.notchian.client;

import java.util.ArrayList;
import java.util.List;

import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Zlib;
import net.pocketdreams.sequinland.notchian.translator.PocketPacketTranslator;

public class NotchianPlayer extends Player {
    public Session session;

    public NotchianPlayer(Session session, Long clientID, String ip, int port) {
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
        
        Class<? extends PocketPacketTranslator> clazz = PocketPacketTranslator.translators.get(packet.getClass());
        if (clazz != null) {
            try {
                PocketPacketTranslator translator = (PocketPacketTranslator) clazz.newInstance();

                Packet[] packets = translator.translate(packet, this);
                for (Packet pcPacket : packets) {
                    session.send(pcPacket);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
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