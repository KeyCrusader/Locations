package keyboardcrusader.locations.api;

import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class StructureEvent extends Event {
    private final StructureStart<?> structure;
    private final ServerWorld world;

    public StructureEvent(StructureStart<?> structure, ServerWorld world) {
        this.structure = structure;
        this.world = world;
    }

    public StructureStart<?> getStructure() {
        return structure;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    /**
     * Called whenever a valid structure is generated
     */
    public static class Created extends StructureEvent {

        public Created(StructureStart<?> structure, ServerWorld world) {
            super(structure, world);
        }
    }
}
