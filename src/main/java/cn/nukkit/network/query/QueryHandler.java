package cn.nukkit.network.query;

import cn.nukkit.Server;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.utils.Binary;
import io.netty.buffer.Unpooled;
import net.marfgamer.jraknet.RakNetPacket;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class QueryHandler {

    public static final byte HANDSHAKE = 0x09; // Challenge (Handshake)
    public static final byte STATISTICS = 0x00; // Request statistics

    private final Server server;
    private byte[] lastToken;
    private byte[] token;
    private byte[] longData;
    private byte[] shortData;
    private long timeout;

    public QueryHandler() {
        this.server = Server.getInstance();
        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.server.query.start"));
        String ip = this.server.getIp();
        String addr = (!"".equals(ip)) ? ip : "0.0.0.0";
        int port = this.server.getPort();
        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.server.query.info", String.valueOf(port)));

        this.regenerateToken();
        this.lastToken = this.token;
        this.regenerateInfo();
        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.server.query.running", new String[]{addr, String.valueOf(port)}));
    }

    public void regenerateInfo() {
        QueryRegenerateEvent ev = this.server.getQueryInformation();
        this.longData = ev.getLongQuery();
        this.shortData = ev.getShortQuery();
        this.timeout = System.currentTimeMillis() + ev.getTimeout();
    }

    public void regenerateToken() {
        this.lastToken = this.token;
        byte[] token = new byte[16];
        for (int i = 0; i < 16; i++) {
            token[i] = (byte) new Random().nextInt(255);
        }
        this.token = token;
    }

    public static String getTokenString(byte[] token, String salt) {
        return getTokenString(new String(token), salt);
    }


    public static String getTokenString(String token, String salt) {
        try {
            return String.valueOf(Binary.readInt(Binary.subBytes(MessageDigest.getInstance("SHA-512").digest((salt + ":" + token).getBytes()), 7, 4)));
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(new Random().nextInt());
        }
    }

    public void handle(InetSocketAddress address, RakNetPacket packet) {
        // First: 0x09 (Handshake/Challenge)
        // Second: 0x00 (Statistics)
        byte packetType = packet.readByte(); // Read query type
        // Session ID (binary-packed timestamp)
        int sessionId = packet.readInt();
        byte[] payload = packet.read(packet.remaining());
        byte[] reply;
        
        switch (packetType) {
        case HANDSHAKE:
            reply = Binary.appendBytes(
                    HANDSHAKE, // 0x09, this is a reply for the handshake/challenge request
                    Binary.writeInt(sessionId), // This is the session ID that we received before
                    getTokenString(this.token, address.getHostString()).getBytes(),
                    new byte[]{0x00}
                    );

            this.server.getRakNetInterface().getJRakNetServer().sendNettyMessage(Unpooled.copiedBuffer(reply), address);
            return;
        case STATISTICS:
            String token = String.valueOf(Binary.readInt(Binary.subBytes(payload, 0, 4)));
            if (!token.equals(getTokenString(this.token, address.getHostString())) && !token.equals(getTokenString(this.lastToken, address.getHostString()))) {
                break;
            }

            if (this.timeout < System.currentTimeMillis()) {
                this.regenerateInfo();
            }
            
            reply = Binary.appendBytes(
                    STATISTICS,
                    Binary.writeInt(sessionId),
                    payload.length == 8 ? this.longData : this.shortData
                    );

            this.server.getRakNetInterface().getJRakNetServer().sendNettyMessage(Unpooled.copiedBuffer(reply), address);
            break;
        }
    }
}
