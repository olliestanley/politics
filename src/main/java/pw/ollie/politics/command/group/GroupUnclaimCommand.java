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
package pw.ollie.politics.command.group;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.group.GroupPlotUnclaimEvent;
import pw.ollie.politics.event.plot.PlotOwnerChangeEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.message.MessageUtil;
import pw.ollie.politics.world.plot.Plot;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class GroupUnclaimCommand extends GroupSubcommand {
    GroupUnclaimCommand(GroupLevel groupLevel) {
        super("unclaim", groupLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.UNCLAIM) && !hasAdmin(sender)) {
            throw new CommandException("You don't have permissions to unclaim land in this " + level.getName() + ".");
        }

        Location location = findLocation(sender, args);
        if (!plugin.getWorldManager().getWorld(location.getWorld()).getConfig().hasPlots()) {
            throw new CommandException("There are no plots in that world.");
        }

        Plot plot = plugin.getWorldManager().getPlotAt(location);
        if (!plot.isOwner(group)) {
            throw new CommandException("Sorry, this plot is not owned by " + group.getName() + ".");
        }

        if (!plot.removeOwner()) {
            throw new CommandException("The plot could not be unclaimed.");
        }

        GroupPlotUnclaimEvent claimEvent = PoliticsEventFactory.callGroupPlotUnclaimEvent(group, plot, sender);
        if (claimEvent.isCancelled()) {
            throw new CommandException("You cannot unclaim this plot!");
        }

        PlotOwnerChangeEvent ownerChangeEvent = PoliticsEventFactory.callPlotOwnerChangeEvent(plot, group.getUid(), false);
        if (ownerChangeEvent.isCancelled()) {
            throw new CommandException("Your group cannot relinquish ownership of this plot!");
        }

        MessageUtil.message(sender, "The plot was claimed successfully");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".unclaim";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/" + level.getId() + " unclaim [-g " + level.getName() + "] [-u universe]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Unclaims land for a " + level.getName() + ".";
    }
}
