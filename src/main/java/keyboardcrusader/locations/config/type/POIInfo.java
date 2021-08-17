package keyboardcrusader.locations.config.type;

import keyboardcrusader.locations.Locations;
import net.minecraft.util.ResourceLocation;

public class POIInfo extends LocationInfo {
    private boolean connected;
    private boolean inLocation;

    public POIInfo(boolean disabled, int distance, ResourceLocation atlasMarker, boolean connected, boolean inLocation) {
        super(disabled, distance, atlasMarker);
        this.connected = connected;
        this.inLocation = inLocation;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isInLocation() {
        return inLocation;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setInLocation(boolean inLocation) {
        this.inLocation = inLocation;
    }

    public POIInfo() {
        this(false, 300, new ResourceLocation(Locations.MODID, "none"), false, true);
    }

    @Override
    public String serializeCommon() {
        return super.serializeCommon() + ";" + isConnected() + ";" + isInLocation();
    }

    @Override
    public void deserializeCommon(String string) {
        String[] strings = string.split(";");
        super.deserializeCommon(string);
        setConnected(Boolean.parseBoolean(strings[1]));
        setInLocation(Boolean.parseBoolean(strings[2]));
    }
}
