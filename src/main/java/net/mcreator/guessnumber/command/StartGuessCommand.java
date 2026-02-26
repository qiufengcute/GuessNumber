package net.mcreator.guessnumber.command;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandBuildContext;

import net.mcreator.guessnumber.procedures.StartGuessRunProcedure;

import net.fabricmc.fabric.api.entity.FakePlayer;

import com.mojang.brigadier.CommandDispatcher;

public class StartGuessCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
		dispatcher.register(Commands.literal("startguess")

				.executes(arguments -> {
					Level world = arguments.getSource().getLevel();
					double x = arguments.getSource().getPosition().x();
					double y = arguments.getSource().getPosition().y();
					double z = arguments.getSource().getPosition().z();
					Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel _servLevel)
						entity = FakePlayer.get(_servLevel);
					Direction direction = Direction.DOWN;
					if (entity != null)
						direction = entity.getDirection();

					StartGuessRunProcedure.execute(world, x, y, z, entity);
					return 0;
				}));
	}
}