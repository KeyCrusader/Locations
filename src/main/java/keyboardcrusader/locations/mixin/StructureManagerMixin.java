package keyboardcrusader.locations.mixin;

import keyboardcrusader.locations.api.StructureEvent;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fires an event when a structure is created in the world
// Structure generation appears to be done on different threads so defer back to main server thread

@Mixin(StructureManager.class)
public class StructureManagerMixin {
    @Shadow @Final private IWorld world;

    @Inject(method = "addStructureStart", at = @At("TAIL"))
    private void onStructureCreated(SectionPos sectionPos, Structure<?> structure, StructureStart<?> start, IStructureReader reader, CallbackInfo ci) {
        if (start.isValid() && this.world instanceof ServerWorld) {
            ServerLifecycleHooks.getCurrentServer().deferTask(() -> {
                MinecraftForge.EVENT_BUS.post(new StructureEvent.Created(start, (ServerWorld) this.world));
            });
        }
    }
}
