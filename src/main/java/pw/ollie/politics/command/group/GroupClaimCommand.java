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

import pw.ollie.politics.Politics;
import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.group.GroupClaimPlotEvent;
import pw.ollie.politics.event.plot.PlotOwnerChangeEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.Position;
import pw.ollie.politics.util.message.MessageUtil;
import pw.ollie.politics.world.plot.Plot;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class GroupClaimCommand extends GroupSubCommand {
    GroupClaimCommand(GroupLevel groupLevel) {
        super("claim", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        if (!groupLevel.canOwnLand()) {
            throw new CommandException(groupLevel.getPlural() + " cannot claim land other than through sub-organisations.");
        }

        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.CLAIM) && !hasAdmin(sender)) {
            throw new CommandException("You don't have permissions to claim land in this " + groupLevel.getName() + ".");
        }

        Location location = findLocation(sender, args);
        Position position = Position.fromLocation(location);
        if (!group.getUniverse().getWorlds().contains(Politics.getWorld(position.getWorld()))) {
            throw new CommandException("You can't create a plot for that group in this world.");
        }

        Plot plot = plugin.getWorldManager().getPlotAtChunk(location.getChunk());
        if (plot.isOwner(group)) {
            throw new CommandException(group.getName() + " already owns this plot.");
        }

        Group owner = plot.getOwner();
        if (owner != null) {
            throw new CommandException("Sorry, this plot is already owned by " + owner.getName() + ".");
        }

        if (!plot.setOwner(group)) {
            throw new CommandException("You cannot claim this plot!");
        }

        GroupClaimPlotEvent claimEvent = PoliticsEventFactory.callGroupClaimPlotEvent(group, plot, sender);
        if (claimEvent.isCancelled()) {
            throw new CommandException("You cannot claim this plot!");
        }

        PlotOwnerChangeEvent ownerChangeEvent = PoliticsEventFactory.callPlotOwnerChangeEvent(plot, group.getUid(), true);
        if (ownerChangeEvent.isCancelled()) {
            throw new CommandException("Your group cannot own this plot!");
        }

        MessageUtil.message(sender, "The plot was claimed successfully");
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".claim";
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " claim [-g " + groupLevel.getName() + "] [-u universe]";
    }

    @Override
    public String getDescription() {
        return "Claims land for a " + groupLevel.getName() + ".";
    }
}
