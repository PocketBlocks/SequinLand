package net.pocketdreams.sequinland.notchian.translator;

import java.util.HashMap;

import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.notch.PCClientChatPacketTranslator;
import net.pocketdreams.sequinland.notchian.translator.notch.PCClientPlayerActionPacketTranslator;
import net.pocketdreams.sequinland.notchian.translator.notch.PCClientPlayerMovementPacket;
import net.pocketdreams.sequinland.notchian.translator.notch.PCClientPlayerPositionPacketTranslator;
import net.pocketdreams.sequinland.notchian.translator.notch.PCClientPlayerPositionRotationPacket;
import net.pocketdreams.sequinland.notchian.translator.notch.PCClientPlayerRotationPacketTranslator;
import net.pocketdreams.sequinland.notchian.translator.notch.PCClientPlayerStatePacketTranslator;

// Translate PC packets to Pocket packets
public abstract class NotchPacketTranslator {
    public static HashMap<Class<? extends Packet>, Class<? extends NotchPacketTranslator>> translators = new HashMap<>();

    public abstract DataPacket[] translate(Packet packet, NotchianPlayer player);
    
    static {
        translators.put(ClientChatPacket.class, PCClientChatPacketTranslator.class);
        translators.put(ClientPlayerPositionRotationPacket.class, PCClientPlayerPositionRotationPacket.class);
        translators.put(ClientPlayerMovementPacket.class, PCClientPlayerMovementPacket.class);
        translators.put(ClientPlayerPositionPacket.class, PCClientPlayerPositionPacketTranslator.class);
        translators.put(ClientPlayerRotationPacket.class, PCClientPlayerRotationPacketTranslator.class);
        translators.put(ClientPlayerStatePacket.class, PCClientPlayerStatePacketTranslator.class);
        translators.put(ClientPlayerActionPacket.class, PCClientPlayerActionPacketTranslator.class);
    }
}
