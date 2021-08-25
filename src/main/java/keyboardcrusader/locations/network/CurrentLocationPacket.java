package keyboardcrusader.locations.network;

import keyboardcrusader.locations.api.LocationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CurrentLocationPacket implements IPacket {
    private final Long id;
    private final boolean discovery;

    public CurrentLocationPacket(Long id, boolean discovery) {
        this.id = id;
        this.discovery = discovery;
    }

    public CurrentLocationPacket(PacketBuffer buf) {
        id = buf.readLong();
        discovery = buf.readBoolean();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeLong(id);
        buf.writeBoolean(discovery);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().getRenderViewEntity();
            if (!(entity instanceof PlayerEntity)) throw new IllegalArgumentException("Entity not player");
            PlayerEntity playerEntity = (PlayerEntity) entity;

            LocationHelper.setCurrentLocation(playerEntity, id, discovery);
        });
        ctx.get().setPacketHandled(true);
    }
}
