package net.pocketdreams.sequinland.utils;

public enum OperatingSystem {
    UNKNOWN(0),
    ANDROID(1),
    IOS(2),
    FIRE_OS(3),
    GEAR_VR(4),
    APPLE_TV(5),
    FIRE_TV(6),
    WINDOWS_10(7);
    
    private int id;
    
    OperatingSystem(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public static OperatingSystem getById(int id) {
        for (OperatingSystem os : OperatingSystem.values()) {
            if (os.getId() == id) {
                return os;
            }
        }
        return OperatingSystem.UNKNOWN;
    }
}
