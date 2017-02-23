package net.pocketdreams.sequinland.notchian.translator.pocket;

import java.util.ArrayList;

import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.data.game.PlayerListEntry;
import org.spacehq.mc.protocol.data.game.PlayerListEntryAction;
import org.spacehq.mc.protocol.data.game.entity.player.GameMode;
import org.spacehq.mc.protocol.data.message.Message;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.network.protocol.PlayerListPacket.Entry;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.PocketPacketTranslator;

public class PEPlayerListPacketTranslator extends PocketPacketTranslator {

    @Override
    public Packet[] translate(DataPacket packet, NotchianPlayer player) {
        PlayerListPacket pePacket = (PlayerListPacket) packet;

        if (pePacket.type == PlayerListPacket.TYPE_ADD) {
            ArrayList<PlayerListEntry> entries = new ArrayList<>();
            
            for (Entry entry : pePacket.entries) {
                entries.add(new PlayerListEntry(new GameProfile(entry.uuid, entry.name), GameMode.SURVIVAL, 0, Message.fromString(entry.name)));
            }
            ServerPlayerListEntryPacket pcPacket = new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, entries.toArray(new PlayerListEntry[0]));
            return new Packet[] { pcPacket };
        } else {
            return new Packet[] {};
        }
        
    }

}
