package cn.nukkit.blockentity;

import java.util.Objects;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntitySign extends BlockEntitySpawnable {

    public BlockEntitySign(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        if (!nbt.contains("Text1")) {
            nbt.putString("Text1", "");
        }
        if (!nbt.contains("Text2")) {
            nbt.putString("Text2", "");
        }
        if (!nbt.contains("Text3")) {
            nbt.putString("Text3", "");
        }
        if (!nbt.contains("Text4")) {
            nbt.putString("Text4", "");
        }
        this.namedTag = nbt;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.remove("Creator");
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = getBlock().getId();
        return blockID == Block.SIGN_POST || blockID == Block.WALL_SIGN;
    }

    public boolean setText() {
        return this.setText("");
    }

    public boolean setText(String line1) {
        return this.setText(line1, "");
    }

    public boolean setText(String line1, String line2) {
        return this.setText(line1, line2, "");
    }

    public boolean setText(String line1, String line2, String line3) {
        return this.setText(line1, line2, line3, "");
    }

    public boolean setText(String line1, String line2, String line3, String line4) {
        this.namedTag.putString("Text1", line1);
        this.namedTag.putString("Text2", line2);
        this.namedTag.putString("Text3", line3);
        this.namedTag.putString("Text4", line4);
        this.spawnToAll();

        if (this.chunk != null) {
            this.chunk.setChanged();
            this.level.clearChunkCache(this.chunk.getX(), this.chunk.getZ());
        }

        return true;
    }

    public String[] getText() {
        return new String[]{
                this.namedTag.getString("Text1"),
                this.namedTag.getString("Text2"),
                this.namedTag.getString("Text3"),
                this.namedTag.getString("Text4")
        };
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.SIGN)
                .putString("Text1", this.namedTag.getString("Text1"))
                .putString("Text2", this.namedTag.getString("Text2"))
                .putString("Text3", this.namedTag.getString("Text3"))
                .putString("Text4", this.namedTag.getString("Text4"))
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

    }

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player p) {
        SignChangeEvent signChangeEvent = new SignChangeEvent(this.getBlock(), p, new String[]{
                p.getRemoveFormat() ? TextFormat.clean(nbt.getString("Text1")) : nbt.getString("Text1"),
                p.getRemoveFormat() ? TextFormat.clean(nbt.getString("Text2")) : nbt.getString("Text2"),
                p.getRemoveFormat() ? TextFormat.clean(nbt.getString("Text3")) : nbt.getString("Text3"),
                p.getRemoveFormat() ? TextFormat.clean(nbt.getString("Text4")) : nbt.getString("Text4")
        });

        if (!this.namedTag.contains("Creator") || !Objects.equals(p.getUniqueId().toString(), this.namedTag.getString("Creator"))) {
            signChangeEvent.setCancelled();
        }

        this.server.getPluginManager().callEvent(signChangeEvent);

        if (!signChangeEvent.isCancelled()) {
            ((BlockEntitySign) this).setText(signChangeEvent.getLine(0), signChangeEvent.getLine(1), signChangeEvent.getLine(2), signChangeEvent.getLine(3));
            return true;
        } else {
            ((BlockEntitySign) this).spawnTo(p);
            return false;
        }
    }
}
