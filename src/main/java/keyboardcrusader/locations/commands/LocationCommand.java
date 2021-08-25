package keyboardcrusader.locations.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import keyboardcrusader.locations.api.LocationHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.ArrayList;

public class LocationCommand {
    public static final SimpleCommandExceptionType LOCATION_MISSING_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("locations.commands.missing"));
    public static final SimpleCommandExceptionType LOCATION_CURRENT_EMPTY = new SimpleCommandExceptionType(new TranslationTextComponent("locations.commands.empty"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> locationCommand
                = Commands.literal("locations")
                .requires((commandSource) -> commandSource.hasPermissionLevel(2))
                .then(Commands.literal("current")
                        // Default to self
                        .executes(LocationCommand::currentNoTarget)
                        // Set a target
                        .then(Commands.argument("target", EntityArgument.player())
                            .executes(LocationCommand::currentWithTarget))
                )
                .then(Commands.literal("rename")
                    .then(Commands.argument("name", StringArgumentType.string())
                            .executes(LocationCommand::renameNoTarget))
                    .then(Commands.argument("id", LongArgumentType.longArg())
                        .then(Commands.argument("name", StringArgumentType.string())
                            .executes(LocationCommand::renameWithTarget)))
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("id", LongArgumentType.longArg())
                            .then(Commands.literal("global")
                                .executes(LocationCommand::removeNoTarget))
                            .then(Commands.argument("target", EntityArgument.player())
                                .executes(LocationCommand::removeWithTarget)))
                );

        dispatcher.register(locationCommand);
    }

    static void showMessage(CommandContext<CommandSource> commandContext, String message) {
        Entity entity = commandContext.getSource().getEntity();
        ITextComponent currentLocation = ITextComponent.getTextComponentOrEmpty(message);
        if (entity != null) {
            commandContext.getSource().getServer().getPlayerList().func_232641_a_(currentLocation, ChatType.CHAT, entity.getUniqueID());
            //func_232641_a_ is sendMessage()
        }
        else {
            commandContext.getSource().getServer().getPlayerList().func_232641_a_(currentLocation, ChatType.SYSTEM, Util.DUMMY_UUID);
        }
    }

    static int currentWithTarget(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        current(EntityArgument.getPlayer(commandContext, "target"), commandContext);
        return 1;
    }

    static int currentNoTarget(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        current(commandContext.getSource().asPlayer(), commandContext);
        return 1;
    }

    static void current(PlayerEntity targetPlayer, CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        long id = LocationHelper.currentLocation(targetPlayer);
        if (id == 0) {
            throw LOCATION_CURRENT_EMPTY.create();
        }
        showMessage(commandContext, targetPlayer.getDisplayName().getString() + " current location: " + LocationHelper.currentLocation(targetPlayer).toString());
    }

    static int renameWithTarget(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        long id = LongArgumentType.getLong(commandContext, "id");
        String name = StringArgumentType.getString(commandContext, "name");

        rename(commandContext.getSource().getWorld(), id, name, commandContext);

        return 1;
    }

    static int renameNoTarget(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        long id = LocationHelper.currentLocation(commandContext.getSource().asPlayer());
        if (id == 0) {
            throw LOCATION_CURRENT_EMPTY.create();
        }
        rename(commandContext.getSource().getWorld(), id, StringArgumentType.getString(commandContext, "name"), commandContext);
        return 1;
    }

    static void rename(ServerWorld world, Long id, String name, CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        if (LocationHelper.isDiscovered(world, id)) {
            LocationHelper.renameGlobal(world, id, name);
            showMessage(commandContext, "Renamed " + id + " to " + name);
        }
        else {
            // Not found
            throw LOCATION_MISSING_EXCEPTION.create();
        }
    }

    static int removeWithTarget(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        long id = LongArgumentType.getLong(commandContext, "id");

        PlayerEntity playerEntity = EntityArgument.getPlayer(commandContext, "target");
        remove(playerEntity, id, commandContext);

        return 1;
    }

    static int removeNoTarget(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        long id = LongArgumentType.getLong(commandContext, "id");

        remove(commandContext.getSource().getWorld(), id, commandContext);
        return 1;
    }

    static void remove(ICapabilityProvider capabilityProvider, Long id, CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        if (!LocationHelper.isDiscovered(capabilityProvider, id)) {
            throw LOCATION_MISSING_EXCEPTION.create();
        }

        String message;
        if (capabilityProvider instanceof ServerWorld) {
            LocationHelper.removeGlobal((ServerWorld) capabilityProvider, Lists.newArrayList(id));
            message = "Everyone";
        }
        else {
            LocationHelper.remove(capabilityProvider, id);
            message = ((PlayerEntity) capabilityProvider).getScoreboardName();
        }
        showMessage(commandContext, message + " forgot about " + id);
    }
}
