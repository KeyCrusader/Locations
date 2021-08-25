package keyboardcrusader.locations.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class Config {
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final Client CLIENT = new Client(CLIENT_BUILDER);
    public static final ForgeConfigSpec clientSpec = CLIENT_BUILDER.build();

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final Common COMMON = new Common(COMMON_BUILDER);
    public static final ForgeConfigSpec commonSpec = COMMON_BUILDER.build();

    public static void save() {
        COMMON.serialize();
        commonSpec.save();
        if (FMLEnvironment.dist == Dist.CLIENT) Config.saveClient();
    }

    private static void saveClient() {
        CLIENT.serialize();
        clientSpec.save();
    }
}
