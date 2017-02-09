package cn.nukkit.raknet.protocol;

public class PacketReliability {
    /*
     * From https://github.com/OculusVR/RakNet/blob/master/Source/PacketPriority.h
     *
     * Default: 0b010 (2) or 0b011 (3)
     */
    public static final int UNRELIABLE = 0;
    public static final int UNRELIABLE_SEQUENCED = 1;
    public static final int RELIABLE = 2;
    public static final int RELIABLE_ORDERED = 3;
    public static final int RELIABLE_SEQUENCED = 4;
    public static final int UNRELIABLE_WITH_ACK_RECEIPT = 5;
    public static final int RELIABLE_WITH_ACK_RECEIPT = 6;
    public static final int RELIABLE_ORDERED_WITH_ACK_RECEIPT = 7;
}
