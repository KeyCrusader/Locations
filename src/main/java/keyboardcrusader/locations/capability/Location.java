package keyboardcrusader.locations.capability;

import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.api.LocationHelper;
import keyboardcrusader.locations.config.type.LocationInfo;
import keyboardcrusader.locations.config.type.StructureInfo;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

import java.util.HashSet;
import java.util.Set;

public class Location {
    private BlockPos position;
    private Set<AxisAlignedBB> bounds = new HashSet<>();
    private AxisAlignedBB maxBounds;
    private final RegistryKey<World> dimension;
    private String name;
    private final ResourceLocation type;
    private final Source source;
    private final Boolean permanent;
    private final Boolean multiBlock;


    /**
     * Used for syncing with packets, shouldn't be used to generate any new locations
     */
    public Location(BlockPos position, RegistryKey<World> dimension, String name, ResourceLocation type, Set<AxisAlignedBB> bounds, AxisAlignedBB maxBounds, Source source, boolean permanent, boolean multiBlock) {
        this.position = position;
        this.dimension = dimension;
        this.name = name;
        this.type = type;
        this.bounds = bounds;
        this.maxBounds = maxBounds;
        this.source = source;
        this.permanent = permanent;
        this.multiBlock = multiBlock;
    }

    public Location(StructureStart<?> structureStart, World world) {
        // Add a 1 block buffer to the bounds to add an overlap so that moving between sections of a structure doesn't register as dipping in and out
        structureStart.getComponents().forEach(structurePiece -> this.bounds.add(LocationHelper.mutableToAxisAlignedBB(structurePiece.getBoundingBox()).grow(1)));
        this.maxBounds = LocationHelper.mutableToAxisAlignedBB(structureStart.getBoundingBox());
        this.source = Source.STRUCTURE;
        this.dimension = world.getDimensionKey();
        this.type = structureStart.getStructure().getRegistryName();
        this.position = LocationHelper.getPosition(structureStart.getComponents().get(0));
        this.name = ((StructureInfo) getInfo()).getNameGenerator().generateName(this.type, world.getBiome(position).getTemperature());
        this.permanent = true;
        this.multiBlock = true;
    }

    public Location(Feature<?> feature, World world, BlockPos pos) {
        this.name = LocationsRegistry.NAME_GENERATORS.getValue(LocationsRegistry.NAME_GENERATORS.getDefaultKey()).generateName(feature.getRegistryName(), 1.0F);
        this.source = Source.FEATURE;
        this.dimension = world.getDimensionKey();
        this.type = feature.getRegistryName();
        this.position = pos;
        this.maxBounds = new AxisAlignedBB(pos).grow(5);
        this.bounds.add(this.maxBounds);
        this.permanent = true;
        this.multiBlock = true;
    }

    public Location(PointOfInterestType type, ServerWorld world, BlockPos pos) {
        this.name = LocationsRegistry.NAME_GENERATORS.getValue(LocationsRegistry.NAME_GENERATORS.getDefaultKey()).generateName(type.getRegistryName(), 1.0F);
        this.dimension = world.getDimensionKey();
        this.type = type.getRegistryName();
        this.source = Source.POI;
        this.permanent = LocationsRegistry.POIS.get(getType()).isPermanent();
        this.multiBlock = LocationsRegistry.POIS.get(getType()).isMultiBlock();

        if (multiBlock) {
            // Get all connected, get centre as pos and grow by 5 for bounds
            this.maxBounds = LocationHelper.getMultiBlockLocation(type.getBlockStates(), world, pos).grow(5);
        }
        else {
            this.maxBounds = new AxisAlignedBB(pos);
        }
        this.bounds.add(this.maxBounds);
        this.position = new BlockPos(this.maxBounds.getCenter().getX(), this.maxBounds.getCenter().getY(), this.maxBounds.getCenter().getZ());
    }

    public Location(CompoundNBT nbt) {
        this.position = BlockPos.fromLong(nbt.getLong("position"));
        this.dimension = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString("dimension")));
        this.name = nbt.getString("name");
        this.type = new ResourceLocation(nbt.getString("type"));
        this.source = Source.values()[nbt.getInt("source")];
        this.maxBounds = LocationHelper.fromIntArray(nbt.getIntArray("maxBounds"));
        ListNBT boundsNBT = (ListNBT) nbt.get("bounds");
        for (INBT inbt : boundsNBT) {
            this.bounds.add(LocationHelper.fromIntArray(((IntArrayNBT) inbt).getIntArray()));
        }
        this.permanent = nbt.getBoolean("permanent");
        this.multiBlock = nbt.getBoolean("enterable");
    }

    public Location(Location location) {
        this.position = location.getPosition();
        this.dimension = location.getDimension();
        this.name = location.getName();
        this.type = location.getType();
        this.bounds = location.getBounds();
        this.maxBounds = location.getMaxBounds();
        this.source = location.getSource();
        this.permanent = location.isPermanent();
        this.multiBlock = location.isMultiBlock();
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

    public AxisAlignedBB getMaxBounds() {
        return this.maxBounds;
    }

    public Set<AxisAlignedBB> getBounds() {
        return this.bounds;
    }

    public Source getSource() {
        return this.source;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public Boolean isMultiBlock() {
        return this.multiBlock;
    }

    public boolean isVecInside(Vector3d vec) {
        if (!isMultiBlock()) {
            return false;
        }

        if (getSource() == Source.STRUCTURE && ((StructureInfo) getInfo()).useSquareBounds()) {
            return this.maxBounds.contains(vec);
        }

        for (AxisAlignedBB boundingBox : this.getBounds()) {
            if (boundingBox.contains(vec)) {
                return true;
            }
        }
        return false;
    }

    public CompoundNBT serialize(Long id) {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putLong("id", id);
        nbt.putLong("position", this.getPosition().toLong());
        nbt.putString("dimension", this.getDimension().getLocation().toString());
        nbt.putString("name", this.getName());
        nbt.putString("type", this.getType().toString());
        nbt.putInt("source", this.getSource().ordinal());
        nbt.putIntArray("maxBounds", LocationHelper.toIntArrayNBT(this.getMaxBounds()).getIntArray());

        ListNBT boundsNBT = new ListNBT();
        for (AxisAlignedBB bounds : this.getBounds()) {
            boundsNBT.add(LocationHelper.toIntArrayNBT(bounds));
        }
        nbt.put("bounds", boundsNBT);
        nbt.putBoolean("permanent", this.isPermanent());
        nbt.putBoolean("enterable", this.isMultiBlock());

        return nbt;
    }

    public LocationInfo getInfo() {
        switch(getSource()) {
            case STRUCTURE:
                return LocationsRegistry.STRUCTURES.get(getType());
            case FEATURE:
                return LocationsRegistry.FEATURES.get(getType());
            case POI:
                return LocationsRegistry.POIS.get(getType());
            case DEATH:
            case SPAWN:
                return LocationsRegistry.LOCATIONS.get(getType());
        }
        return null;
    }

    public void update(Location location) {
        position = location.getPosition();
        bounds = location.getBounds();
        maxBounds = location.getMaxBounds();
    }

    public void setName(String string) {
        name = string;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Location)) return false;

        Location l = (Location) o;
        if (!l.getBounds().equals(this.getBounds())) {
            return false;
        }
        if (!l.getMaxBounds().equals(this.getMaxBounds())) {
            return false;
        }
        if (!l.getPosition().equals(this.getPosition())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return this.getName() + ":" + this.getMaxBounds();
    }

    public enum Source {
        STRUCTURE,
        FEATURE,
        POI,
        DEATH,
        SPAWN
    }
}
