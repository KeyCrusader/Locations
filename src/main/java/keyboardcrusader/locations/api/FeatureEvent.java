package keyboardcrusader.locations.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.eventbus.api.Event;

public class FeatureEvent extends Event {
    private final Feature<?> feature;
    private final IFeatureConfig config;
    private final World world;
    private final BlockPos pos;

    public FeatureEvent(Feature<?> feature, IFeatureConfig config, World world, BlockPos pos) {
        this.feature = feature;
        this.config = config;
        this.world = world;
        this.pos = pos;
    }

    public Feature<?> getFeature() {
        return feature;
    }

    public IFeatureConfig getConfig() {
        return config;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public static class Created extends FeatureEvent {
        public Created(Feature<?> feature, IFeatureConfig config, World world, BlockPos pos) {
            super(feature, config, world, pos);
        }
    }
}
