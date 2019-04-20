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
package pw.ollie.politics.command.plot;

import pw.ollie.politics.Politics;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.PoliticsCommandHelper;
import pw.ollie.politics.command.PoliticsSubCommand;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.world.plot.Plot;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class PlotSubCommand extends PoliticsSubCommand {
    protected PlotSubCommand(String name) {
        super(name);
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
