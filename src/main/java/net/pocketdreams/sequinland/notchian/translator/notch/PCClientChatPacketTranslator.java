package net.pocketdreams.sequinland.notchian.translator.notch;

import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.TextPacket;
import co.aikar.timings.Timings;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.NotchPacketTranslator;

public class PCClientChatPacketTranslator extends NotchPacketTranslator {

    @Override
    public DataPacket[] translate(Packet packet, NotchianPlayer player) {
        ClientChatPacket pcPacket = (ClientChatPacket) packet;
        if (pcPacket.getMessage().startsWith("/")) {
            PlayerCommandPreprocessEvent playerCommandPreprocessEvent = new PlayerCommandPreprocessEvent(player, "/" + pcPacket.getMessage().substring(1));
            cn.nukkit.Server.getInstance().getPluginManager().callEvent(playerCommandPreprocessEvent);
            if (playerCommandPreprocessEvent.isCancelled()) {
                return new DataPacket[] {};
            }

            Timings.playerCommandTimer.startTiming();
            cn.nukkit.Server.getInstance().dispatchCommand(playerCommandPreprocessEvent.getPlayer(), playerCommandPreprocessEvent.getMessage().substring(1));
            Timings.playerCommandTimer.stopTiming();
            return new DataPacket[] {};
        }
        TextPacket pk = new TextPacket();
        pk.type = TextPacket.TYPE_CHAT;
        pk.message = pcPacket.getMessage();
        pk.source = player.getName();
        return new DataPacket[] { pk };
    }

}
