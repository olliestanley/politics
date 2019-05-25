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
import pw.ollie.politics.command.PoliticsCommandHelper;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.group.GroupMemberJoinEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// note: admin command for force adding a player to a group
public class GroupAddCommand extends GroupSubcommand {
    GroupAddCommand(GroupLevel groupLevel) {
        super("add", groupLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        if (!level.hasImmediateMembers()) {
            throw new CommandException("You cannot add to a " + level.getName() + " other than through a sub-organisation.");
        }

        Group group = findGroup(sender, args);

        if (!hasAdmin(sender)) {
            throw new CommandException("You can't do that.");
        }

        if (args.length(false) < 1) {
            throw new CommandException("There must be a player specified to add.");
        }

        String playerName = args.getString(0, false);
        Player player = plugin.getServer().getPlayer(playerName);
        if (player == null) {
            throw new CommandException("That player is not online.");
        }

        if (!level.allowedMultiple() && plugin.getGroupManager().hasGroupOfLevel(player, level)) {
            throw new CommandException("The player is already part of a " + level.getName() + ".");
        }

        GroupMemberJoinEvent joinEvent = PoliticsEventFactory.callGroupMemberJoinEvent(group, player, level.getInitial());
        if (joinEvent.isCancelled()) {
            throw new CommandException("You may not add that player to that " + level.getName() + ".");
        }

        group.setRole(player.getUniqueId(), level.getInitial());
        MessageBuilder.begin("Added the player to the ").append(level.getName()).append(" with role ")
                .highlight(level.getInitial().getName()).normal(".").send(sender);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return PoliticsCommandHelper.GROUPS_ADMIN_PERMISSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/" + level.getId() + " add <player> [-g " + level.getName() + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Force adds a player to a " + level.getName() + ".";
    }
}
