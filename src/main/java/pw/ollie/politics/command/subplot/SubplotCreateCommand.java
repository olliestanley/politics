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

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.activity.ActivityManager;
import pw.ollie.politics.activity.PoliticsActivity;
import pw.ollie.politics.activity.activities.CuboidSelectionActivity;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.math.Cuboid;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.world.WorldConfig;
import pw.ollie.politics.world.WorldManager;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.Subplot;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

// starts process create a new subplot in current plot
public class SubplotCreateCommand extends SubplotSubcommand {
    SubplotCreateCommand() {
        super("create");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        WorldManager worldManager = plugin.getWorldManager();
        Player player = (Player) sender;
        WorldConfig worldConfig = worldManager.getWorld(player.getWorld()).getConfig();
        if (!worldConfig.hasPlots() || !worldConfig.hasSubplots()) {
            throw new CommandException("There are no subplots in this world.");
        }

        Plot currentPlot = worldManager.getPlotAt(player.getLocation());
        Group ownerGroup = currentPlot.getOwner();
        if (ownerGroup == null || !(ownerGroup.can(player, Privileges.GroupPlot.MANAGE_SUBPLOTS) || hasPlotsAdmin(sender))) {
            throw new CommandException("You aren't in the plot of an organisation you're permitted to do that for.");
        }

        ActivityManager activityManager = plugin.getActivityManager();
        if (activityManager.isActive(player)) {
            throw new CommandException("You already have an ongoing task you must finish before starting a new one.");
        }

        PoliticsActivity activity = new CuboidSelectionActivity(player.getUniqueId(), selection -> {
            Plot plot = worldManager.getPlotAt(selection.getFirstPoint());
            if (!plot.equals(worldManager.getPlotAt(selection.getSecondPoint()))) {
                MessageBuilder.beginError().append("Both corners of the subplot must be inside the same plot.").send(sender);
                return;
            }

            Group group = plot.getOwner();
            if (group == null || !group.can(player, Privileges.GroupPlot.MANAGE_SUBPLOTS)) {
                MessageBuilder.beginError().append("You aren't in the plot of an organisation you're permitted to do that for.").send(sender);
            }

            Cuboid cuboid = selection.getCuboid();
            for (Subplot existingSubplot : plot.getSubplots()) {
                Cuboid existingCuboid = existingSubplot.getCuboid();
                if (existingCuboid.intersects(cuboid)) {
                    MessageBuilder.beginError().append("The selection overlaps an existing subplot!").send(sender);
                    return;
                }
            }

            Subplot subplot = new Subplot(plot.getWorld(), plot.generateSubplotId(), plot.getBaseX(), plot.getBaseZ(),
                    cuboid, player.getUniqueId());
            if (plot.addSubplot(subplot)) {
                MessageBuilder.begin("Successfully created subplot!").send(sender);
            } else {
                MessageBuilder.beginError().append("Failed to create subplot.").send(sender);
            }
        });

        MessageBuilder.begin("Please select the maximum and minimum points of the subplot area by left-clicking blocks.").send(sender);
        activityManager.beginActivity(activity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return "politics.plot.subplot.create";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/subplot create";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Adds a subplot to your current plot";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAliases() {
        return Arrays.asList("new", "add");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
