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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LocationPacket implements IPacket {
    private final List<Location> locations;
    private final PacketType packetType;

    public LocationPacket(Location location, PacketType packetType) {
        this.locations = Lists.newArrayList(location);
        this.packetType = packetType;
    }
    public LocationPacket(List<Location> locations, PacketType packetType) {
        this.locations = locations;
        this.packetType = packetType;
    }

    public LocationPacket(PacketBuffer buf) {
        int count = buf.readInt();
        this.locations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            locations.add(new Location(
                    buf.readLong(),
                    BlockPos.fromLong(buf.readLong()),
                    RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(buf.readString())),
                    buf.readString(),
                    new ResourceLocation(buf.readString()),
                    new MutableBoundingBox(buf.readVarIntArray()),
                    Location.Source.values()[buf.readInt()]
            ));
        }
        packetType = PacketType.values()[buf.readInt()];
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(locations.size());
        for (Location location : locations) {
            buf.writeLong(location.getID());
            buf.writeLong(location.getPosition().toLong());
            buf.writeString(location.getDimension().getRegistryName().toString());
            buf.writeString(location.getName());
            buf.writeString(location.getType().toString());
            buf.writeVarIntArray(location.getBounds().toNBTTagIntArray().getIntArray());
            buf.writeInt(location.getSource().ordinal());
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
                    for (Location location : locations) {
                        LocationHelper.discover(playerEntity, location);
                    }
                    break;
                case UPDATE:
                    for (Location location : locations) {
                        LocationHelper.update(playerEntity, location);
                    }                    break;
                case REMOVE:
                    for (Location location : locations) {
                        LocationHelper.remove(playerEntity, location);
                    }                    break;
                case SYNC:
                    for (Location location : locations) {
                        playerEntity.getCapability(LocationsCap.LOCATIONS_CAPABILITY).orElseThrow(() -> new IllegalArgumentException(playerEntity.getDisplayName().getString()+" missing locations capability")).discover(location);
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