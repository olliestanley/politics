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
import pw.ollie.politics.event.group.GroupMemberJoinEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GroupJoinCommand extends GroupSubCommand {
    GroupJoinCommand(GroupLevel groupLevel) {
        super("join", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        if (args.length(false) < 1) {
            throw new CommandException("There was no " + groupLevel.getName() + " specified to join.");
        }

        Group group = plugin.getGroupManager().getGroupByTag(args.getString(0, false));
        if (group == null) {
            throw new CommandException("That " + groupLevel.getName() + " does not exist.");
        }

        // safe cast as isPlayerOnly() returns true
        Player player = (Player) sender;
        if (!group.getBooleanProperty(GroupProperty.OPEN, false) && !group.isInvited(player)) {
            throw new CommandException("That " + groupLevel.getName() + " is closed and you don't have an invitation.");
        }

        GroupMemberJoinEvent joinEvent = plugin.getEventFactory().callGroupMemberJoinEvent(group, player, groupLevel.getInitial());
        if (joinEvent.isCancelled()) {
            throw new CommandException("You may not join that " + groupLevel.getName() + ".");
        }

        UUID playerId = player.getUniqueId();
        group.setRole(playerId, joinEvent.getRole());
        MessageBuilder.begin("Successfully joined ").highlight(group.getStringProperty(GroupProperty.NAME))
                .normal(".").send(sender);
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".join";
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " join <" + groupLevel.getName() + ">";
    }

    @Override
    public String getDescription() {
        return "Joins the " + groupLevel.getName() + ".";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
