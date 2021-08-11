package keyboardcrusader.locations.events.forge;

import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.capability.ILocationsCap;
import keyboardcrusader.locations.capability.LocationsCap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Locations.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEventHandler {
    @SubscribeEvent
    public static void attachCapability(final AttachCapabilitiesEvent event) {
        if (event.getObject() instanceof PlayerEntity || event.getObject() instanceof World) {
            ICapabilityProvider object = (ICapabilityProvider) event.getObject();
            if (!object.getCapability(LocationsCap.LOCATIONS_CAPABILITY).isPresent()) {
                event.addCapability(
                        new ResourceLocation(Locations.MODID, "locations"),
                        new ICapabilitySerializable<ListNBT>() {
                            private final LocationsCap locations = new LocationsCap();
                            private final LazyOptional<ILocationsCap> locationsCap = LazyOptional.of(() -> locations);

                            @Nonnull
                            @Override
                            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
                                if (capability == LocationsCap.LOCATIONS_CAPABILITY) {
                                    return (LazyOptional<T>) locationsCap;
                                } else {
                                    return LazyOptional.empty();
                                }
                            }


                            @Override
                            public ListNBT serializeNBT() {
                                return (ListNBT) LocationsCap.LOCATIONS_CAPABILITY.writeNBT(this.locations, null);
                            }

                            @Override
                            public void deserializeNBT(ListNBT nbt) {
                                LocationsCap.LOCATIONS_CAPABILITY.readNBT(this.locations, null, nbt);
                            }
                        });
            }
        }
    }
}
