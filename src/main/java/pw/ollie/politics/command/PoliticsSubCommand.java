/*
 * This file is part of Politics.
 *
 * Copyright (c) 2019 Oliver Stanley
 * Politics is licensed under the Affero General Public License Version 3.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pw.ollie.politics.command;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.args.Arguments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic subcommand of any command in Politics.
 */
public abstract class PoliticsSubCommand {
    private final String name;

    protected PoliticsSubCommand(String name) {
        this.name = name.toLowerCase();
    }

    public abstract void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException;

    public String getName() {
        return name;
    }

    /**
     * Finds a location from the given context, first checking to see if one was specified by flag, then falling back on
     * the location of the command sender if they are a player.
     *
     * @param sender  the source of the command
     * @param context the arguments provided
     * @return the location relevant to the context
     */
    protected Location findLocation(CommandSender sender, Arguments context) throws CommandException {
        if (context.hasValueFlag("l")) {
            String locationArg = context.getValueFlag("l").getStringValue();
            String[] split = locationArg.split(",");
            if (split.length < 3 || split.length > 4) {
                throw new CommandException("Please specify coordinates in the format <-p x,y,z> or <-p world,x,y,z>");
            }
            if (split.length == 3 && !(sender instanceof Player)) {
                throw new CommandException("Please specify coordinates in the format <world,x,y,z>");
            }

            World world;
            String xArg, yArg, zArg;
            if (split.length == 3) {
                world = ((Player) sender).getWorld();
                xArg = split[0];
                yArg = split[1];
                zArg = split[2];
            } else {
                world = Bukkit.getWorld(split[0]);
                if (world == null) {
                    throw new CommandException("The provided world is not a world.");
                }
                xArg = split[1];
                yArg = split[2];
                zArg = split[3];
            }

            int x, y, z;
            try {
                x = Integer.parseInt(xArg);
                y = Integer.parseInt(yArg);
                z = Integer.parseInt(zArg);
            } catch (NumberFormatException e) {
                throw new CommandException("The provided x or z coordinate was not an integer.");
            }

            return new Location(world, x, y, z);
        }

        if (sender instanceof Player) {
            return ((Player) sender).getLocation();
        }

        throw new CommandException("You must specify a location using the flag -l.");
    }

    public abstract String getPermission();

    /**
     * Gets list of aliases. If there are none this should return an empty list, *not* {@code null}.
     * <p>
     * All entries in this list should be *lower case*.
     *
     * @return string list of aliases
     */
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    public abstract String getUsage();

    public abstract String getDescription();

    /**
     * Whether command is player-only. Should be overridden by subcommands which are.
     *
     * @return true if command is only for players, else false
     */
    public boolean isPlayerOnly() {
        return false;
    }
}
