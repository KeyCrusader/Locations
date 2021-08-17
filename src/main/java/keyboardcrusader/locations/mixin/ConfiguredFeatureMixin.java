package keyboardcrusader.locations.mixin;

import keyboardcrusader.locations.api.FeatureEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

// Fires an event when a feature is created
// Feature generation appears to be done on different threads so defer back to main server thread

@Mixin(ConfiguredFeature.class)
public abstract class ConfiguredFeatureMixin<FC extends IFeatureConfig, F extends Feature<FC>> {
    @Shadow public abstract F getFeature();

    @Shadow public abstract FC getConfig();

    @Inject(method = "generate", at = @At("TAIL"))
    private void onFeatureCreated(ISeedReader reader, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            ServerLifecycleHooks.getCurrentServer().deferTask(() -> {
                MinecraftForge.EVENT_BUS.post(new FeatureEvent.Created(this.getFeature(), this.getConfig(), reader.getWorld(), pos));
            });
        }
    }
}
