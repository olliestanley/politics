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

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.PoliticsSubcommand;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.visualise.Visualisation;
import pw.ollie.politics.world.plot.Plot;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlotViewCommand extends PoliticsSubcommand {
    PlotViewCommand() {
        super("view");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Player player = (Player) sender;
        if (!plugin.getWorldManager().getWorld(player.getWorld()).getConfig().hasPlots()) {
            throw new CommandException("There are no plots in this world.");
        }
        Plot plot = plugin.getWorldManager().getPlotAt(player.getLocation());
        Visualisation visualisation = plugin.getVisualiser().visualisePlot(plot);
        if (!visualisation.apply(plugin.getVisualiser(), player)) {
            throw new CommandException("You must complete or cancel your current activity first.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return "politics.plot.view";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/plot view";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Visualise the outline of your current plot";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
