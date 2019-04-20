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
package pw.ollie.politics.command.subplot;

import pw.ollie.politics.Politics;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.PoliticsCommandHelper;
import pw.ollie.politics.command.PoliticsSubCommand;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.Subplot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class SubplotSubCommand extends PoliticsSubCommand {
    protected SubplotSubCommand(String name) {
        super(name);
    }

    protected Subplot findSubplot(CommandSender sender, String relevantArg, Arguments context) throws CommandException {
        if (relevantArg.equalsIgnoreCase("here")) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can use 'here' to specify a location.");
            }

            Location location = ((Player) sender).getLocation();
            return Politics.getWorldManager().getPlotAt(location).getSubplotAt(location);
        }

        if (!relevantArg.contains(",")) {
            int subplotId;
            try {
                subplotId = Integer.parseInt(relevantArg);
            } catch (NumberFormatException e) {
                throw new CommandException("The specified subplot id is not valid.");
            }

            Subplot result = findPlot(sender, context).getSubplot(subplotId);
            if (result == null) {
                throw new CommandException("There is no subplot with that id in the specified plot.");
            }
            return result;
        }

        String[] split = relevantArg.split(",");
        if (split.length < 3 || split.length > 4) {
            throw new CommandException("Please specify plot coordinates in the format <x,y,z> or <world,x,y,z>");
        }
        if (split.length == 3 && !(sender instanceof Player)) {
            throw new CommandException("Please specify plot coordinates in the format <world,x,y,z>");
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
        Subplot result = Politics.getWorldManager().getPlotAt(location).getSubplotAt(location);
        if (result == null) {
            throw new CommandException("There is no subplot at that location.");
        }
        return result;
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

    public boolean hasAdmin(CommandSender source) {
        return source instanceof ConsoleCommandSender || source.hasPermission(PoliticsCommandHelper.PLOTS_ADMIN_PERMISSION);
    }
}
