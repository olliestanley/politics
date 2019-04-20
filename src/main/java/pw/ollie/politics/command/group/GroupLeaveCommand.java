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
import pw.ollie.politics.event.group.GroupMemberLeaveEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GroupLeaveCommand extends GroupSubCommand {
    GroupLeaveCommand(GroupLevel groupLevel) {
        super("leave", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        if (!group.isImmediateMember(playerId)) {
            throw new CommandException("You are not a member of that " + groupLevel.getName() + ".");
        }

        GroupMemberLeaveEvent leaveEvent = PoliticsEventFactory.callGroupMemberLeaveEvent(group, player, false);
        if (leaveEvent.isCancelled()) {
            throw new CommandException("You cannot leave the " + groupLevel.getName() + ".");
        }

        group.removeRole(playerId);
        MessageBuilder.begin("Successfully left ").highlight(group.getName()).normal(".").send(sender);
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("quit");
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".leave";
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " leave [-g " + groupLevel.getName() + "]";
    }

    @Override
    public String getDescription() {
        return "Leaves your " + groupLevel.getName() + ".";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
