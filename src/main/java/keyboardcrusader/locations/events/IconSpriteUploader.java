package keyboardcrusader.locations.events;

import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.api.LocationHelper;
import keyboardcrusader.locations.capability.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.stream.Stream;

public class IconSpriteUploader extends SpriteUploader {
    public static IconSpriteUploader LOCATION_ICONS_SPRITES;

    public IconSpriteUploader(TextureManager textureManagerIn) {
        super(textureManagerIn, new ResourceLocation(Locations.MODID, "textures/atlas/icons.png"), "icons");
    }

    @Override
    protected Stream<ResourceLocation> getResourceLocations() {
        // Load in all of the available icons from the icons folder
        Collection<ResourceLocation> textureLocations;
        textureLocations = Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/icons", s -> s.endsWith(".png"));

        return textureLocations.stream()
                .filter(resourceLocation -> Locations.MODID.equals(resourceLocation.getNamespace()))
                // 15 is the length of "textures/icons/" & 4 is the length of ".png"
                .map(rl -> new ResourceLocation(rl.getNamespace(), rl.getPath().substring(15, rl.getPath().length() - 4)));
    }

    public TextureAtlasSprite getSprite(Location location) {
        try {
            return this.getSprite(new ResourceLocation(Locations.MODID, LocationHelper.getLocationInfo(location).getIcon()));
        }
        catch (NullPointerException e) {
            Locations.LOGGER.error("Sprite "+ LocationHelper.getLocationInfo(location).getIcon() + " not found, using default");
            return this.getSprite(new ResourceLocation(Locations.MODID, "default"));
        }
    }
}
