package keyboardcrusader.locations.capability;

import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.config.type.LocationInfo;
import keyboardcrusader.locations.config.type.StructureInfo;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class Location {
    private BlockPos position;
    private RegistryKey<World> dimension;
    private String name;
    private ResourceLocation type;
    private List<MutableBoundingBox> bounds = new ArrayList<>();
    private MutableBoundingBox maxBounds;
    private Source source;

    /**
     * Used for syncing with packets, shouldn't be used to generate any new locations
     */
    public Location(BlockPos position, RegistryKey<World> dimension, String name, ResourceLocation type, List<MutableBoundingBox> bounds, MutableBoundingBox maxBounds, Source source) {
        this.position = position;
        this.dimension = dimension;
        this.name = name;
        this.type = type;
        this.bounds = bounds;
        this.maxBounds = maxBounds;
        this.source = source;
    }

    public Location(StructureStart<?> structureStart, World world) {
        structureStart.getComponents().forEach(structurePiece -> this.bounds.add(structurePiece.getBoundingBox()));
        this.maxBounds = structureStart.getBoundingBox();
        this.source = Source.STRUCTURE;
        this.dimension = world.getDimensionKey();
        this.type = structureStart.getStructure().getRegistryName();
        this.position = getPosition(structureStart.getComponents().get(0));
        this.name = ((StructureInfo) getInfo()).getNameGenerator().generateName(this.type, world.getBiome(position).getTemperature());
    }

    public Location(Feature<?> feature, World world, BlockPos pos) {
        this.name = LocationsRegistry.NAME_GENERATORS.getValue(LocationsRegistry.NAME_GENERATORS.getDefaultKey()).generateName(feature.getRegistryName(), 1.0F);
        this.source = Source.FEATURE;
        this.dimension = world.getDimensionKey();
        this.type = feature.getRegistryName();
        this.position = pos;
        this.maxBounds = new MutableBoundingBox(
                pos.getX() - 5,
                pos.getY() - 5,
                pos.getZ() - 5,
                pos.getX() + 5,
                pos.getY() + 5,
                pos.getZ() + 5);
        this.bounds.add(this.maxBounds);
    }

    public Location(PointOfInterestType type, ServerWorld world, BlockPos pos) {
        this.name = LocationsRegistry.NAME_GENERATORS.getValue(LocationsRegistry.NAME_GENERATORS.getDefaultKey()).generateName(type.getRegistryName(), 1.0F);
        this.source = Source.POI;
        this.dimension = world.getDimensionKey();
        this.type = type.getRegistryName();
        this.position = pos;
        this.maxBounds = new MutableBoundingBox(pos, pos);
        this.bounds.add(this.maxBounds);
    }

    public Location(PointOfInterestType type, ServerWorld world, BlockPos pos, boolean permanent) {
        this.name = LocationsRegistry.NAME_GENERATORS.getValue(LocationsRegistry.NAME_GENERATORS.getDefaultKey()).generateName(type.getRegistryName(), 1.0F);
        this.source = Source.PERMANENT_POI;
        this.dimension = world.getDimensionKey();
        this.type = type.getRegistryName();
        this.position = pos;
        this.maxBounds = new MutableBoundingBox(
                pos.getX() - 5,
                pos.getY() - 5,
                pos.getZ() - 5,
                pos.getX() + 5,
                pos.getY() + 5,
                pos.getZ() + 5);
        this.bounds.add(this.maxBounds);
    }

    public Location(CompoundNBT nbt) {
        this.position = BlockPos.fromLong(nbt.getLong("position"));
        this.dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString("dimension")));
        this.name = nbt.getString("name");
        this.type = new ResourceLocation(nbt.getString("type"));
        this.source = Source.values()[nbt.getInt("source")];
        this.maxBounds = new MutableBoundingBox(nbt.getIntArray("maxBounds"));
        ListNBT boundsNBT = (ListNBT) nbt.get("bounds");
        for (INBT inbt : boundsNBT) {
            this.bounds.add(new MutableBoundingBox(((IntArrayNBT) inbt).getIntArray()));
        }
    }

    public BlockPos getPosition() {
        return position;
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

    public MutableBoundingBox getMaxBounds() {
        return this.maxBounds;
    }

    public List<MutableBoundingBox> getBounds() {
        return this.bounds;
    }

    public Source getSource() {
        return this.source;
    }

    public boolean isVecInside(Vector3i vec) {
        if (getSource() == Source.STRUCTURE && ((StructureInfo) getInfo()).useSquareBounds()) {
            return this.maxBounds.isVecInside(vec);
        }

        for (MutableBoundingBox boundingBox : this.getBounds()) {
            if (boundingBox.isVecInside(vec)) {
                return true;
            }
        }
        return false;
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

    public CompoundNBT serialize(Long id) {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putLong("id", id);
        nbt.putLong("position", this.getPosition().toLong());
        nbt.putString("dimension", this.getDimension().getLocation().toString());
        nbt.putString("name", this.getName());
        nbt.putString("type", this.getType().toString());
        nbt.putInt("source", this.getSource().ordinal());
        nbt.putIntArray("maxBounds", this.getMaxBounds().toNBTTagIntArray().getIntArray());

        ListNBT boundsNBT = new ListNBT();
        for (MutableBoundingBox bounds : this.getBounds()) {
            boundsNBT.add(bounds.toNBTTagIntArray());
        }
        nbt.put("bounds", boundsNBT);

        return nbt;
    }

    public LocationInfo getInfo() {
        switch(getSource()) {
            case STRUCTURE:
                return LocationsRegistry.STRUCTURES.get(getType());
            case FEATURE:
                return LocationsRegistry.FEATURES.get(getType());
            case MAP:
                return LocationsRegistry.MAP_MARKERS.get(getType());
            case POI:
            case PERMANENT_POI:
                return LocationsRegistry.POIS.get(getType());
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Location)) return false;

        Location l = (Location) o;
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
        return this.getName() + ":" + this.getPosition();
    }
    
    public static BlockPos getPosition(StructurePiece structurePiece) {
        return new BlockPos(
                structurePiece.getBoundingBox().minX + (structurePiece.getBoundingBox().getXSize() / 2),
                structurePiece.getBoundingBox().minY + (structurePiece.getBoundingBox().getYSize() / 2),
                structurePiece.getBoundingBox().minZ + (structurePiece.getBoundingBox().getZSize() / 2));
    }

    public static Long generateID(ResourceLocation type, RegistryKey<World> dimension, BlockPos position) {
        return (Math.abs(type.hashCode()) + Math.abs(dimension.getLocation().hashCode()) + Math.abs(position.toLong())) % Long.MAX_VALUE;
    }

    public enum Source {
        STRUCTURE,
        MAP,
        FEATURE,
        POI,
        PERMANENT_POI,
        DEATH
    }
}
