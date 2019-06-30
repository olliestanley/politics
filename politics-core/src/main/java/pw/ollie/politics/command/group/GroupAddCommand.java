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

import gnu.trove.map.hash.THashMap;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.PoliticsCommandHelper;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.group.GroupMemberJoinEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.util.message.MessageKeys;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static pw.ollie.politics.util.message.VarMapFiller.*;

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
            throw new CommandException(MessageKeys.COMMAND_GROUP_ADD_NO_IMMEDIATE_MEMBERS, map("level", level.getName()));
        }

        Group group = findGroup(sender, args);

        if (!hasAdmin(sender)) {
            throw new CommandException(MessageKeys.COMMAND_NO_PERMISSION);
        }

        if (args.length(false) < 1) {
            throw new CommandException(MessageKeys.COMMAND_SPECIFY_PLAYER);
        }

        String playerName = args.getString(0, false);
        Player player = plugin.getServer().getPlayer(playerName);
        if (player == null) {
            throw new CommandException(MessageKeys.COMMAND_PLAYER_OFFLINE);
        }

        if (!level.allowedMultiple() && plugin.getGroupManager().hasGroupOfLevel(player, level)) {
            throw new CommandException(MessageKeys.COMMAND_GROUP_ADD_PLAYER_HAS_GROUP, map("level", level.getName()));
        }

        GroupMemberJoinEvent joinEvent = PoliticsEventFactory.callGroupMemberJoinEvent(group, player, level.getInitial());
        if (joinEvent.isCancelled()) {
            throw new CommandException(MessageKeys.COMMAND_GROUP_ADD_DISALLOWED, map("level", level.getName()));
        }

        group.setRole(player.getUniqueId(), level.getInitial());
        plugin.sendConfiguredMessage(sender, MessageKeys.COMMAND_GROUP_ADD_SUCCESS,
                filler().vars("level", "role").vals(level.getName(), level.getInitial().getName()).fill(new THashMap<>(2)));
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
