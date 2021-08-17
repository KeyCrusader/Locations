package keyboardcrusader.locations.mixin;

import keyboardcrusader.locations.api.PointOfInterestEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;

// Fires an event when a point of interest is created or removed

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow public abstract ServerWorld getWorld();

    @Inject(method = "onBlockStateChange", at = @At("TAIL"))
    private void pointOfInterestUpdated(BlockPos pos, BlockState blockStateIn, BlockState newState, CallbackInfo ci) {
        Optional<PointOfInterestType> optional = PointOfInterestType.forState(blockStateIn);
        Optional<PointOfInterestType> optional1 = PointOfInterestType.forState(newState);
        if (!Objects.equals(optional, optional1)) {
            BlockPos blockpos = pos.toImmutable();
            optional.ifPresent((type) -> {
                MinecraftForge.EVENT_BUS.post(new PointOfInterestEvent.Removed(type, getWorld(), blockpos));
            });
            optional1.ifPresent((type) -> {
                MinecraftForge.EVENT_BUS.post(new PointOfInterestEvent.Created(type, getWorld(), blockpos));
            });
        }
    }
}
