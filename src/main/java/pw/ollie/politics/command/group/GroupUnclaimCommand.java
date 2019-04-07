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
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.world.plot.Plot;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GroupUnclaimCommand extends GroupSubCommand {
    GroupUnclaimCommand(GroupLevel groupLevel) {
        super("unclaim", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.UNCLAIM) && !hasAdmin(sender)) {
            throw new CommandException("You don't have permissions to unclaim land in this " + groupLevel.getName() + ".");
        }

        // todo update for regionplots when added

        // TODO add a way to get the world, x, y, z from the command line
        // (should be in GroupCommand)
        Location location = ((Player) sender).getLocation();

        Plot plot = plugin.getPlotManager().getChunkPlotAt(location);
        if (plot == null) {
            throw new CommandException("There is no plot here!");
        }
        if (!plot.isOwner(group)) {
            throw new CommandException("Sorry, this plot is not owned by " + group.getStringProperty(GroupProperty.NAME) + ".");
        }

        if (!plot.removeOwner(group)) {
            throw new CommandException("The plot could not be unclaimed.");
        }

        sender.sendMessage("The plot was unclaimed successfully.");
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".unclaim";
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " unclaim [-g " + groupLevel.getName() + "] [-u universe]";
    }

    @Override
    public String getDescription() {
        return "Unclaims land for a " + groupLevel.getName() + ".";
    }
}
