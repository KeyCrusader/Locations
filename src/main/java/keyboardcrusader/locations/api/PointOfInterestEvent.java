package keyboardcrusader.locations.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class PointOfInterestEvent extends Event {
    private final PointOfInterestType type;
    private final ServerWorld world;
    private final BlockPos pos;

    public PointOfInterestEvent(PointOfInterestType type, ServerWorld world, BlockPos pos) {
        this.type = type;
        this.world = world;
        this.pos = pos;
    }

    public PointOfInterestType getType() {
        return type;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public static class Created extends PointOfInterestEvent {
        public Created(PointOfInterestType type, ServerWorld world, BlockPos pos) {
            super(type, world, pos);
        }
    }

    public static class Removed extends PointOfInterestEvent {
        public Removed(PointOfInterestType type, ServerWorld world, BlockPos pos) {
            super(type, world, pos);
        }
    }
}
