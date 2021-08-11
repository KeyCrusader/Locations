package keyboardcrusader.locations.mixin;

import keyboardcrusader.locations.network.MapPacket;
import keyboardcrusader.locations.network.PacketHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Syncs the map data to the client, required for banners and map markers to be able to have locations

@Mixin(MapData.class)
public class MapDataMixin extends WorldSavedData{
    @Shadow public int xCenter;
    @Shadow public int zCenter;
    @Shadow public RegistryKey<World> dimension;
    @Shadow public byte scale;

    public MapDataMixin(String name) {
        super(name);
    }

    @Inject(method = "initData", at = @At("TAIL"))
    private void onInit(int x, int z, int scale, boolean trackingPosition, boolean unlimitedTracking, RegistryKey<World> dimension, CallbackInfo ci) {
        MapPacket.updateMap(this.getName(), this.xCenter, this.zCenter, this.scale, this.dimension);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void onRead(CompoundNBT nbt, CallbackInfo ci) {
        PacketHandler.HANDLER.send(PacketDistributor.ALL.noArg(), new MapPacket(this.getName(), this.xCenter, this.zCenter, this.dimension, this.scale));
    }

    @Shadow
    public void read(CompoundNBT nbt) {
        throw new IllegalStateException("Mixin failed to shadow read()");
    }

    @Shadow
    public CompoundNBT write(CompoundNBT compound) {
        throw new IllegalStateException("Mixin failed to shadow write()");
    }
}
