package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.Zlib;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


/**
 * Created by on 15-10-13.
 */
public class LoginPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LOGIN_PACKET;

    public String username;
    public int protocol;
    public byte gameEdition;
    public UUID clientUUID;
    public long clientId;
    public String identityPublicKey;
    public String serverAddress;
    public String deviceModel;
    public int deviceOperatingSystem;
    public HashMap<String, JsonElement> clientData = new HashMap<>();
    public Skin skin;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.protocol = this.getInt();
        
        if (this.protocol != ProtocolInfo.CURRENT_PROTOCOL) {
            this.setBuffer(null);
            return;
        }
        
        this.gameEdition = (byte) this.getByte();
        byte[] str;
        try {
            str = Zlib.inflate(this.get((int) this.getUnsignedVarInt()));
        } catch (Exception e) {
            return;
        }
        this.setBuffer(str, 0);
        decodeChainData();
        decodeSkinData();
    }

    @Override
    public void encode() {

    }

    public int getProtocol() {
        return protocol;
    }

    private void decodeChainData() {
        Map<String, List<String>> map = new Gson().fromJson(new String(this.get(getLInt()), StandardCharsets.UTF_8),
                new TypeToken<Map<String, List<String>>>() {
                }.getType());
        if (map.isEmpty() || !map.containsKey("chain") || map.get("chain").isEmpty()) return;
        List<String> chains = map.get("chain");
        for (String c : chains) {
            JsonObject chainMap = decodeToken(c);
            if (chainMap == null) continue;
            if (chainMap.has("extraData")) {
                JsonObject extra = chainMap.get("extraData").getAsJsonObject();
                if (extra.has("displayName")) this.username = extra.get("displayName").getAsString();
                if (extra.has("identity")) this.clientUUID = UUID.fromString(extra.get("identity").getAsString());
            }
            if (chainMap.has("identityPublicKey"))
                this.identityPublicKey = chainMap.get("identityPublicKey").getAsString();
        }
    }

    private void decodeSkinData() {
        JsonObject skinToken = decodeToken(new String(this.get(this.getLInt())));
        String skinId = null;
        for (Entry<String, JsonElement> entry : skinToken.entrySet()) {
            clientData.put(entry.getKey(), entry.getValue());
        }
        if (clientData.containsKey("ClientRandomId")) this.clientId = clientData.get("ClientRandomId").getAsLong();
        if (clientData.containsKey("ServerAddress")) this.serverAddress = clientData.get("ServerAddress").getAsString();
        if (clientData.containsKey("SkinId")) skinId = clientData.get("SkinId").getAsString();
        if (clientData.containsKey("SkinData")) this.skin = new Skin(clientData.get("SkinData").getAsString(), skinId);
        if (clientData.containsKey("DeviceModel")) this.deviceModel = clientData.get("DeviceModel").getAsString();
        if (clientData.containsKey("DeviceOS")) this.deviceOperatingSystem = clientData.get("DeviceOS").getAsInt();
    }

    private JsonObject decodeToken(String token) {
        String[] base = token.split("\\.");
        if (base.length < 2) return null;
        return new Gson().fromJson(new String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8), JsonObject.class);
    }

    @Override
    public Skin getSkin() {
        return this.skin;
    }
}
