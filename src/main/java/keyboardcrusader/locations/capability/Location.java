package keyboardcrusader.locations.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;

public class Location {
    private final long id;
    private BlockPos position;
    private RegistryKey<World> dimension;
    private String name;
    private ResourceLocation type;
    private MutableBoundingBox bounds;
    private Source source;

    public Location(long id, BlockPos position, RegistryKey<World> dimension, String name, ResourceLocation type, MutableBoundingBox bounds, Source source) {
        this.id = id;
        this.position = position;
        this.dimension = dimension;
        this.name = name;
        this.type = type;
        this.bounds = bounds;
        this.source = source;
    }
    public Location(BlockPos position, RegistryKey<World> dimension, String name, ResourceLocation type, MutableBoundingBox bounds, Source source) {
        this(generateID(position, dimension, type), position, dimension, name, type, bounds, source);
    }
    public Location(BlockPos position, RegistryKey<World> dimension, String name, Structure<?> type, MutableBoundingBox bounds, Source source) {
        this(position, dimension, name, type.getStructure().getRegistryName(), bounds, source);
    }

    public Location(CompoundNBT nbt) {
        this.id = nbt.getLong("id");
        this.position = BlockPos.fromLong(nbt.getLong("position"));
        this.dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString("dimension")));
        this.name = nbt.getString("name");
        this.type = new ResourceLocation(nbt.getString("type"));
        this.bounds = new MutableBoundingBox(nbt.getIntArray("bounds"));
        this.source = Source.values()[nbt.getInt("source")];
    }

    public long getID() {
        return this.id;
    }

    public BlockPos getPosition() {
        return position;
    }

    public BlockPos getCenter() {
        return new BlockPos(this.getBounds().minX + (this.getBounds().getXSize() / 2), this.getBounds().minY + (this.getBounds().getYSize() / 2), this.getBounds().minZ + (this.getBounds().getZSize() / 2));
    }

    public RegistryKey<World> getDimension() {
        return this.dimension;
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation getType() {
        return this.type;
    }

    public MutableBoundingBox getBounds() {
        return this.bounds;
    }

    public Source getSource() {
        return this.source;
    }

    public void update(Location location) {
        if (!this.position.equals(location.getPosition())) {
            this.position = location.getPosition();
        }
        if (this.dimension != location.getDimension()) {
            this.dimension = location.getDimension();
        }
        if (!this.name.equals(location.getName())) {
            this.name = location.getName();
        }
        if (!this.type.equals(location.getType())) {
            this.type = location.getType();
        }
        if (!this.bounds.equals(location.getBounds())) {
            this.bounds = location.getBounds();
        }
    }

    public CompoundNBT serialize() {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putLong("id", this.getID());
        nbt.putLong("position", this.getPosition().toLong());
        nbt.putString("dimension", this.getDimension().getLocation().toString());
        nbt.putString("name", this.getName());
        nbt.putString("type", this.getType().toString());
        nbt.putIntArray("bounds", this.getBounds().toNBTTagIntArray().getIntArray());
        nbt.putInt("source", this.getSource().ordinal());

        return nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Location)) return false;

        Location l = (Location) o;
        if (l.getID() != this.getID()) {
            //Locations.LOGGER.debug("ID mismatch");
            return false;
        }
        if (!l.getPosition().equals(this.getPosition())) {
            //Locations.LOGGER.debug("Position mismatch");
            return false;
        }
        if (l.getDimension() != this.getDimension()) {
            //Locations.LOGGER.debug("Dimension mismatch");
            return false;
        }
        if (!l.getName().equals(this.getName())) {
            //Locations.LOGGER.debug("Name mismatch");
            return false;
        }
        if (!l.getType().equals(this.getType())) {
            //Locations.LOGGER.debug("Type mismatch");
            return false;
        }
        if (l.getBounds().equals(this.getBounds())) {
            //Locations.LOGGER.debug("Bounds mismatch");
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return this.getID() + ":" + this.getName();
    }

    public static long generateID(BlockPos position, RegistryKey<World> dimension, ResourceLocation type) {
        return (Math.abs(type.hashCode()) + Math.abs(dimension.getLocation().hashCode()) + Math.abs(position.toLong())) % Long.MAX_VALUE;
    }

    public enum Source {
        WORLD,
        MAP,
        DEATH
    }
}
