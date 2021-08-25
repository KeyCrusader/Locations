package keyboardcrusader.locations.api;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.marker.DimensionMarkersData;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Helpers methods for use with AntiqueAtlas
 */
public class AtlasHelper {
    /**
     * Checks if a marker already exists in an atlas
     * @param markerType Resource location of the marker, eg 'new ResourceLocation(Locations.MODID, "waystone")'
     * @param atlasID ID of the atlas, integer
     * @param world Current player world
     * @param pos Position to check for the marker
     * @return Integer list of all matching IDs
     */
    public static List<Integer> getMarkerFromLocation(ResourceLocation markerType, int atlasID, World world, BlockPos pos) {
        DimensionMarkersData data = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world)
                .getMarkersDataInWorld(world.getDimensionKey());

        List<Integer> idList = new ArrayList<>();
        List<Marker> markers = data.getMarkersAtChunk((pos.getX() >> 4) / MarkersData.CHUNK_STEP, (pos.getZ() >> 4) / MarkersData.CHUNK_STEP);
        if (markers != null) {
            for (Marker marker : markers) {
                if (marker.getType().equals(markerType)) {
                    if (marker.getX() == pos.getX() && marker.getZ() == pos.getZ()) {
                        // Found the marker.
                        idList.add(marker.getId());
                    }
                }
            }
        }
        return idList;
    }
}
