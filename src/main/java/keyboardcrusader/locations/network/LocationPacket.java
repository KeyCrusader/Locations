package keyboardcrusader.locations.network;

import keyboardcrusader.locations.api.LocationHelper;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.capability.LocationsCap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class LocationPacket implements IPacket {
    private final Map<Long, Location> locations;
    private final PacketType packetType;

    public LocationPacket(Long id, Location location, PacketType packetType) {
        this.locations = new HashMap<>();
        this.locations.put(id, location);
        this.packetType = packetType;
    }
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
            MutableBoundingBox maxBounds = new MutableBoundingBox(buf.readVarIntArray());
            List<MutableBoundingBox> bounds = new ArrayList<>();
            int boundsCount = buf.readInt();
            for (int j = 0; j < boundsCount; j++) {
                bounds.add(new MutableBoundingBox(buf.readVarIntArray()));
            }
            Location.Source source = Location.Source.values()[buf.readInt()];

            this.locations.put(id, new Location(pos, dimension, name, type, bounds, maxBounds, source));
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
            buf.writeVarIntArray(mapEntry.getValue().getMaxBounds().toNBTTagIntArray().getIntArray());
            buf.writeInt(mapEntry.getValue().getBounds().size());
            for (MutableBoundingBox bounds : mapEntry.getValue().getBounds()) {
                buf.writeVarIntArray(bounds.toNBTTagIntArray().getIntArray());
            }
            buf.writeInt(mapEntry.getValue().getSource().ordinal());
        }
        buf.writeInt(packetType.ordinal());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().getRenderViewEntity();
            if (!(entity instanceof PlayerEntity)) throw new IllegalArgumentException("Entity not player");
            PlayerEntity playerEntity = (PlayerEntity) entity;

            switch(packetType) {
                case DISCOVER:
                    for (Map.Entry<Long, Location> mapEntry : locations.entrySet()) {
                        LocationHelper.discover(playerEntity, mapEntry.getKey(), mapEntry.getValue());
                    }
                    break;
                case UPDATE:
                    for (Map.Entry<Long, Location> mapEntry : locations.entrySet()) {
                        LocationHelper.update(playerEntity, mapEntry.getKey(), mapEntry.getValue());
                    }
                    break;
                case REMOVE:
                    for (Map.Entry<Long, Location> mapEntry : locations.entrySet()) {
                        LocationHelper.remove(playerEntity, mapEntry.getKey(), mapEntry.getValue());
                    }                    break;
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
        REMOVE,
        SYNC
    }
}