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
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.TaskUtil;

import org.bukkit.ChatColor;
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
                group.removeRole(playerId);
                sender.sendMessage("Successfully removed the player.");
            } else {
                throw new CommandException("That player is not a member of the " + groupLevel.getName() + ".");
            }

            return;
        }

        TaskUtil.async(plugin, () -> {
            final OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerName);
            final UUID offlinePlayerId = offlinePlayer.hasPlayedBefore() ? offlinePlayer.getUniqueId() : null;

            TaskUtil.sync(plugin, () -> {
                if (offlinePlayerId == null) {
                    // todo might want to change formatting at some point
                    sender.sendMessage(ChatColor.RED + "That player does not exist.");
                } else {
                    if (group.isImmediateMember(offlinePlayerId)) {
                        group.removeRole(offlinePlayerId);
                        sender.sendMessage("Successfully removed the player.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That player is not a member of the " + groupLevel.getName() + ".");
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
        return "/" + groupLevel.getId() + " kick <player> [-g " + groupLevel.getName() + "]";
    }

    @Override
    public String getDescription() {
        return "Kicks a player from the " + groupLevel.getName() + ".";
    }
}
