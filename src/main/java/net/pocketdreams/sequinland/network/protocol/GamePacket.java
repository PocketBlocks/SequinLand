package net.pocketdreams.sequinland.network.protocol;

import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import net.marfgamer.jraknet.Packet;
import net.marfgamer.jraknet.RakNetPacket;
import net.pocketdreams.sequinland.utils.VarInt;

public class GamePacket extends RakNetPacket {    
    public GamePacket() {
        super(0xFE);
    }

    public GamePacket(byte[] data) {
        super(data);
    }

    public GamePacket(Packet packet) {
        super(packet);
    }
    

    /**
     * Reads an unsigned varint
     * 
     * @return An unsigned varint
     */
    public int readUnsignedVarInt() {
        return VarInt.readUnsignedVarInt(this);
    }

    /**
     * Reads a signed varint
     * 
     * @return A signed varint
     */
    public int readSignedVarInt() {
        return VarInt.readSignedVarInt(this);
    }

    /**
     * Reads an unsigned varlong
     * 
     * @return An unsigned varlong
     */
    public long readUnsignedVarLong() {
        return VarInt.readUnsignedVarLong(this);
    }

    /**
     * Reads a signed varlong
     * 
     * @return A signed varlong
     */
    public long readSignedVarLong() {
        return VarInt.readSignedVarLong(this);
    }

    /**
     * Reads a vector of 2 floats
     * 
     * @return A vector of 2 floats
     */
    public Vector2f readVector2f() {
        float x = this.readFloat();
        float y = this.readFloat();
        return new Vector2f(x, y);
    }

    /**
     * Reads a vector of 2 integers
     * 
     * @return A vector of 2 integers
     */
    /*
     * public Vector2i readVector2i() { int x = this.readInt(); int y =
     * this.readInt(); return new Vector2i(x, y); }
     */

    /**
     * Reads a vector of 3 floats
     * 
     * @return A vector of 3 floats
     */
    public Vector3f readVector3f() {
        float x = this.readFloat();
        float y = this.readFloat();
        float z = this.readFloat();
        return new Vector3f(x, y, z);
    }

    /**
     * Reads a byte array from the packet
     * 
     * @return A byte array
     */
    public byte[] readByteArray() {
        int length = this.readUnsignedVarInt();
        return this.read(length);
    }

    /**
     * Reads a string who's length is specified by a varint
     * 
     * @return A string who's length is specified by a varint
     */
    public String readVarString() {
        return new String(this.readByteArray());
    }

    /**
     * Writes an unsigned varint
     * 
     * @param varInt
     *            The varint to write
     * @return The packet
     */
    public GamePacket writeUnsignedVarInt(int varInt) {
        VarInt.writeUnsignedVarInt(this, varInt);
        return this;
    }

    /**
     * Writes a signed varint
     * 
     * @param varInt
     *            The varint to write
     * @return The packet
     */
    public GamePacket writeSignedVarInt(int varInt) {
        VarInt.writeSignedVarInt(this, varInt);
        return this;
    }

    /**
     * Writes an unsigned varlong
     * 
     * @param varLong
     *            The varlong to write
     * @return The packet
     */
    public GamePacket writeUnsignedVarLong(long varLong) {
        VarInt.writeUnsignedVarLong(this, varLong);
        return this;
    }

    /**
     * Writes a signed varlong
     * 
     * @param varLong
     *            The varlong to write
     * @return The packet
     */
    public GamePacket writeSignedVarLong(long varLong) {
        VarInt.writeSignedVarLong(this, varLong);
        return this;
    }

    /**
     * Writes a vector of 2 floats
     * 
     * @param vector
     *            The vector to write
     * @return The packet
     */
    public GamePacket writeVector2f(Vector2f vector) {
        this.writeFloat(vector.x);
        this.writeFloat(vector.y);
        return this;

    }

    /**
     * Writes a vector of 2 integers
     * 
     * @param vector
     *            The vector to write
     * @return The packet
     */
    /*
     * public GamePacket writeVector2i(Vector2i vector) {
     * this.writeInt(vector.x); this.writeInt(vector.y); return this; }
     */

    /**
     * Writes a vector of 3 floats
     * 
     * @param vector
     *            The vector to write
     * @return The packet
     */
    public GamePacket writeVector3f(Vector3f vector) {
        this.writeFloat(vector.x);
        this.writeFloat(vector.y);
        this.writeFloat(vector.z);
        return this;
    }

    /**
     * Writes a vector of 3 integers
     * 
     * @param vector
     *            The vector to write
     * @return The packet
     */
    /*
     * public GamePacket writeVector3i(Vector3i vector) {
     * this.writeInt(vector.x); this.writeInt(vector.y);
     * this.writeInt(vector.z); return this; }
     */

    /**
     * Writes a byte array to the packet
     * 
     * @param data
     *            The data to write
     * @return The packet
     */
    public GamePacket writeByteArray(byte[] data) {
        this.writeUnsignedVarInt(data.length);
        this.write(data);
        return this;
    }

    /**
     * Writes a string who's length is specified by a varint
     * 
     * @param data
     *            The string to write
     * @return The packet
     */
    public GamePacket writeVarString(String data) {
        this.writeByteArray(data.getBytes());
        return this;
    }

    /**
     * If a class that extends <code>GamePacket</code> overrides this and makes
     * it return <code>true</code>, whenever the <code>Player</code> object
     * sends it to a player it will always be compressed no matter the size
     * 
     * @return Whether or not the packet should always be compressed
     */
    public boolean forceCompression() {
        return false;
    }

    public void encode() {
        throw new UnsupportedOperationException();
    }

    public void decode() {
        throw new UnsupportedOperationException();
    }
    
    public byte pid() {
        throw new UnsupportedOperationException();
    }
}
