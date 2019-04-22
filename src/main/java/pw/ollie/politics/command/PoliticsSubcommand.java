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

import pw.ollie.politics.Politics;
import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.group.Citizen;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.Subplot;

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
public abstract class PoliticsSubcommand {
    private final String name;

    protected PoliticsSubcommand(String name) {
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

    protected Citizen getCitizen(GroupLevel level, Player player) {
        Universe universe = getUniverse(level, player);
        if (universe != null) {
            return universe.getCitizen(player.getUniqueId(), player.getName());
        }
        return null;
    }

    protected Universe getUniverse(GroupLevel level, Player player) {
        return Politics.getUniverseManager().getUniverse(player.getWorld(), level);
    }

    /**
     * Gets a Universe - first checking to see if one was specified, then falling back on the one currently relating to
     * the player if there isn't one specified.
     *
     * @param sender  the relevant source of the command
     * @param context the arguments provided
     * @return the universe relevant to the context
     */
    protected Universe findUniverse(GroupLevel level, CommandSender sender, Arguments context) throws CommandException {
        Universe universe;
        if (context.hasValueFlag("u")) {
            String universeName = context.getValueFlag("u").getStringValue();
            universe = Politics.getUniverseManager().getUniverse(universeName);
            if (universe == null) {
                throw new CommandException("There is no universe named '" + universeName + "'");
            }
            return universe;
        }

        if (sender instanceof Player) {
            universe = getUniverse(level, (Player) sender);
            if (universe == null) {
                throw new CommandException("You aren't currently in a universe containing " + level.getPlural() + ".");
            }
        } else {
            throw new CommandException("You must specify a universe.");
        }

        return universe;
    }

    /**
     * Gets a Group - first checking to see if one was specified, then falling back on the one currently relating to
     * the player if there isn't one specified.
     *
     * @param sender  the relevant source of the command
     * @param context the arguments provided
     * @return the group relevant to the context
     */
    protected Group findGroup(GroupLevel level, CommandSender sender, Arguments context) throws CommandException {
        Universe universe = findUniverse(level, sender, context);
        Group group;
        if (context.hasValueFlag("g")) {
            String groupName = context.getValueFlag("g").getStringValue();
            group = universe.getFirstGroupByProperty(level, GroupProperty.TAG, groupName.toLowerCase());
            return group;
        }

        if (sender instanceof Player) {
            group = getCitizen(level, (Player) sender).getGroup(level);
            if (group == null) {
                throw new CommandException("You aren't currently in a " + level.getName() + ".");
            }
        } else {
            throw new CommandException("You must specify a " + level.getName() + ".");
        }

        return group;
    }

    protected Plot findPlot(CommandSender sender, Arguments args) throws CommandException {
        if (args.hasValueFlag("p")) {
            String plotCoords = args.getValueFlag("p").getStringValue();
            String[] split = plotCoords.split(",");
            if (split.length < 2 || split.length > 3) {
                throw new CommandException("Please specify plot coordinates in the format <-p x,z> or <-p world,x,z>");
            }
            if (split.length == 2 && !(sender instanceof Player)) {
                throw new CommandException("Please specify plot coordinates in the format <world,x,z>");
            }

            World world;
            String xArg, zArg;
            if (split.length == 2) {
                world = ((Player) sender).getWorld();
                xArg = split[0];
                zArg = split[1];
            } else {
                world = Bukkit.getWorld(split[0]);
                if (world == null) {
                    throw new CommandException("The provided world is not a world.");
                }
                xArg = split[1];
                zArg = split[2];
            }

            int x, z;
            try {
                x = Integer.parseInt(xArg);
                z = Integer.parseInt(zArg);
            } catch (NumberFormatException e) {
                throw new CommandException("The provided x or z coordinate was not an integer.");
            }

            return Politics.getWorldManager().getPlotAtChunkPosition(world, (int) Math.floor((double) x / 16), (int) Math.floor((double) z / 16));
        }

        if (!(sender instanceof Player)) {
            throw new CommandException("You must specify plot coordinates in the format <-p world,x,z>.");
        }

        return Politics.getWorldManager().getPlotAt(((Player) sender).getLocation());
    }

    protected Subplot findSubplot(CommandSender sender, Arguments args) throws CommandException {
        if (args.hasValueFlag("sp")) {
            String spArg = args.getValueFlag("sp").getStringValue();
            if (spArg.contains(",")) {
                String[] split = spArg.split(",");
                if (split.length < 3 || split.length > 4) {
                    throw new CommandException("Please specify subplot coordinates in the format <-sp x,y,z> or <-sp world,x,y,z>");
                }
                if (split.length == 3 && !(sender instanceof Player)) {
                    throw new CommandException("Please specify subplot coordinates in the format <-sp world,x,y,z>");
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
                    throw new CommandException("A provided x, y or z coordinate was not an integer.");
                }

                Location location = new Location(world, x, y, z);
                Subplot subplot = Politics.getWorldManager().getPlotAt(location).getSubplotAt(location);
                if (subplot == null) {
                    throw new CommandException("You are not situated inside a subplot and did not specify one.");
                }

                return subplot;
            }

            Plot plot = findPlot(sender, args);
            int subplotId;
            try {
                subplotId = Integer.parseInt(spArg);
            } catch (NumberFormatException e) {
                throw new CommandException(spArg + " is not a valid subplot id.");
            }

            Subplot subplot = plot.getSubplot(subplotId);
            if (subplot == null) {
                throw new CommandException(spArg + " is not a valid subplot id.");
            }

            return subplot;
        } else {
            if (!(sender instanceof Player)) {
                throw new CommandException("Please specify subplot coordinates in the format <-sp world,x,y,z>");
            }

            Location location = ((Player) sender).getLocation();
            Subplot subplot = Politics.getWorldManager().getPlotAt(location).getSubplotAt(location);
            if (subplot == null) {
                throw new CommandException("You are not situated inside a subplot and did not specify one.");
            }

            return subplot;
        }
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
