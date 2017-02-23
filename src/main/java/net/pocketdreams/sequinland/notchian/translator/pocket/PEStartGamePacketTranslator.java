package net.pocketdreams.sequinland.notchian.translator.pocket;

import org.spacehq.mc.protocol.data.game.entity.player.GameMode;
import org.spacehq.mc.protocol.data.game.setting.Difficulty;
import org.spacehq.mc.protocol.data.game.world.WorldType;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.packetlib.packet.Packet;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import net.pocketdreams.sequinland.notchian.client.NotchianPlayer;
import net.pocketdreams.sequinland.notchian.translator.PocketPacketTranslator;

public class PEStartGamePacketTranslator extends PocketPacketTranslator {

    @Override
    public Packet[] translate(DataPacket packet, NotchianPlayer player) {
        StartGamePacket pk = (StartGamePacket) packet;
        ServerJoinGamePacket joinPacket = new ServerJoinGamePacket(0, false, GameMode.values()[pk.gamemode], pk.dimension, Difficulty.values()[pk.difficulty], 10, WorldType.DEFAULT, false);

        ServerPlayerPositionRotationPacket rotPacket = new ServerPlayerPositionRotationPacket(pk.x, pk.y, pk.z, 0, 0, 0);
        return new Packet[] { joinPacket, rotPacket };
    }

}
