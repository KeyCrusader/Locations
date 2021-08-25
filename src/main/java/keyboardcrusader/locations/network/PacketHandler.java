package keyboardcrusader.locations.network;

import keyboardcrusader.locations.Locations;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Locations.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;

        HANDLER.registerMessage(id++, LocationPacket.class, LocationPacket::toBytes, LocationPacket::new, LocationPacket::handle);
        HANDLER.registerMessage(id++, CurrentLocationPacket.class, CurrentLocationPacket::toBytes, CurrentLocationPacket::new, CurrentLocationPacket::handle);
    }
}

