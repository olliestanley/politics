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
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.TaskUtil;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.util.message.MessageUtil;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GroupKickCommand extends GroupSubCommand {
    GroupKickCommand(GroupLevel groupLevel) {
        super("kick", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (!hasAdmin(sender) && !group.can(sender, Privileges.Group.KICK)) {
            throw new CommandException("You don't have permission to kick members.");
        }

        if (args.length(false) < 1) {
            throw new CommandException("There was not a player specified to kick.");
        }

        String playerName = args.getString(0, false);
        Player player = plugin.getServer().getPlayer(playerName);
        if (player != null) {
            UUID playerId = player.getUniqueId();
            if (group.isImmediateMember(playerId)) {
                GroupMemberLeaveEvent leaveEvent = PoliticsEventFactory.callGroupMemberLeaveEvent(group, player, true);
                if (leaveEvent.isCancelled()) {
                    throw new CommandException("You cannot kick the player.");
                }

                group.removeRole(playerId);
                MessageUtil.message(sender, "Successfully removed the player.");
            } else {
                throw new CommandException("That player is not a member of the " + level.getName() + ".");
            }

            return;
        }

        TaskUtil.async(plugin, () -> {
            final OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerName);
            final UUID offlinePlayerId = offlinePlayer.hasPlayedBefore() ? offlinePlayer.getUniqueId() : null;

            TaskUtil.sync(plugin, () -> {
                if (offlinePlayerId == null) {
                    MessageBuilder.beginError().append("That player does not exist.").send(sender);
                } else {
                    if (group.isImmediateMember(offlinePlayerId)) {
                        GroupMemberLeaveEvent leaveEvent = PoliticsEventFactory.callGroupMemberLeaveEvent(group, offlinePlayer, true);
                        if (leaveEvent.isCancelled()) {
                            MessageBuilder.beginError().append("You cannot kick the player.").send(sender);
                            return;
                        }

                        group.removeRole(offlinePlayerId);
                        MessageUtil.message(sender, "Successfully removed the player.");
                    } else {
                        MessageBuilder.beginError().append("That player is not a member of the " + level.getName() + ".").send(sender);
                    }
                }
            });
        });
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".kick";
    }

    @Override
    public String getUsage() {
        return "/" + level.getId() + " kick <player> [-g " + level.getName() + "]";
    }

    @Override
    public String getDescription() {
        return "Kicks a player from the " + level.getName() + ".";
    }
}
