package keyboardcrusader.locations.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MapPacket implements IPacket {
    private final String name;
    private final int xCenter;
    private final int zCenter;
    private final RegistryKey<World> dimension;
    private final byte scale;

    public MapPacket(String name, int xCenter, int zCenter, RegistryKey<World> dimension, byte scale) {
        this.name = name;
        this.xCenter = xCenter;
        this.zCenter = zCenter;
        this.dimension = dimension;
        this.scale = scale;
    }

    public MapPacket(PacketBuffer buf) {
        this.name = buf.readString();
        this.xCenter = buf.readInt();
        this.zCenter = buf.readInt();
        this.dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(buf.readString()));
        this.scale = buf.readByte();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeString(this.name);
        buf.writeInt(this.xCenter);
        buf.writeInt(this.zCenter);
        buf.writeString(this.dimension.getRegistryName().toString());
        buf.writeByte(this.scale);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            updateMap(this.name, this.xCenter, this.zCenter, this.scale, this.dimension);
        });
        ctx.get().setPacketHandled(true);
    }
    
    public static void updateMap(String name, int xCenter, int zCenter, byte scale, RegistryKey<World> dimension) {
        ClientWorld world = Minecraft.getInstance().world;
        if (world == null) return;
        MapData mapdata = world.getMapData(name);
        if (mapdata == null) {
            mapdata = new MapData(name);
        }
        mapdata.xCenter = xCenter;
        mapdata.zCenter = zCenter;
        mapdata.dimension = dimension;
        mapdata.scale = scale;
        Minecraft.getInstance().world.registerMapData(mapdata);
    }
}
