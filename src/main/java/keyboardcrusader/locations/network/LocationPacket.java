package keyboardcrusader.locations.network;

import com.google.common.collect.Lists;
import keyboardcrusader.locations.api.LocationHelper;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.capability.LocationsCap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class LocationPacket implements IPacket {
    private final Map<Long, Location> locations;
    private final PacketType packetType;

    public LocationPacket(Map<Long, Location> locations, PacketType packetType) {
        this.locations = locations;
        this.packetType = packetType;
    }

    public LocationPacket(PacketBuffer buf) {
        int count = buf.readInt();
        this.locations = new HashMap<>();
        for (int i = 0; i < count; i++) {
            Long id = buf.readLong();
            BlockPos pos = BlockPos.fromLong(buf.readLong());
            RegistryKey<World> dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(buf.readString()));
            String name = buf.readString();
            ResourceLocation type = ResourceLocation.tryCreate(buf.readString());
            AxisAlignedBB maxBounds = LocationHelper.fromIntArray(buf.readVarIntArray());
            Set<AxisAlignedBB> bounds = new HashSet<>();
            int boundsCount = buf.readInt();
            for (int j = 0; j < boundsCount; j++) {
                bounds.add(LocationHelper.fromIntArray(buf.readVarIntArray()));
            }
            Location.Source source = Location.Source.values()[buf.readInt()];
            boolean permanent = buf.readBoolean();
            boolean enterable = buf.readBoolean();

            this.locations.put(id, new Location(pos, dimension, name, type, bounds, maxBounds, source, permanent, enterable));
        }
        this.packetType = PacketType.values()[buf.readInt()];
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(locations.size());
        for (Map.Entry<Long, Location> mapEntry : locations.entrySet()) {
            buf.writeLong(mapEntry.getKey());
            buf.writeLong(mapEntry.getValue().getPosition().toLong());
            buf.writeString(mapEntry.getValue().getDimension().getRegistryName().toString());
            buf.writeString(mapEntry.getValue().getName());
            buf.writeString(mapEntry.getValue().getType().toString());
            buf.writeVarIntArray(LocationHelper.toIntArrayNBT(mapEntry.getValue().getMaxBounds()).getIntArray());
            buf.writeInt(mapEntry.getValue().getBounds().size());
            for (AxisAlignedBB bounds : mapEntry.getValue().getBounds()) {
                buf.writeVarIntArray(LocationHelper.toIntArrayNBT(bounds).getIntArray());
            }
            buf.writeInt(mapEntry.getValue().getSource().ordinal());
            buf.writeBoolean(mapEntry.getValue().isPermanent());
            buf.writeBoolean(mapEntry.getValue().isMultiBlock());
        }
        buf.writeInt(packetType.ordinal());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().getRenderViewEntity();
            if (!(entity instanceof PlayerEntity)) throw new IllegalArgumentException("Entity not player");
            PlayerEntity playerEntity = (PlayerEntity) entity;

            // Packets that are sent after a player has died, e.g. set spawn point
            if (!playerEntity.isAlive()) return;

            switch(packetType) {
                case DISCOVER:
                    LocationHelper.discover(playerEntity, locations);
                    break;
                case UPDATE:
                    LocationHelper.update(playerEntity, locations);
                    break;
                case RENAME:
                    LocationHelper.rename(playerEntity, locations);
                    break;
                case REMOVE:
                    LocationHelper.remove(playerEntity, Lists.newArrayList(locations.keySet()));
                    break;
                case SYNC:
                    for (Map.Entry<Long, Location> mapEntry : locations.entrySet()) {
                        playerEntity.getCapability(LocationsCap.LOCATIONS_CAPABILITY).orElseThrow(() -> new IllegalArgumentException(playerEntity.getDisplayName().getString()+" missing locations capability")).discover(mapEntry.getKey(), mapEntry.getValue());
                    }
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public enum PacketType {
        DISCOVER,
        UPDATE,
        RENAME,
        REMOVE,
        SYNC
    }
}