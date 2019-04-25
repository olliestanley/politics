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
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GroupInviteCommand extends GroupSubcommand {
    GroupInviteCommand(GroupLevel groupLevel) {
        super("invite", groupLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        if (!level.hasImmediateMembers()) {
            throw new CommandException("You cannot invite to a " + level.getName() + " other than through a sub-organisation.");
        }

        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.INVITE)) {
            throw new CommandException("You don't have permission to invite to the " + level.getName() + ".");
        }

        if (group.getBooleanProperty(GroupProperty.OPEN, false)) {
            throw new CommandException("The " + level.getName() + " is open to join without invitation.");
        }

        if (args.length(false) < 1) {
            throw new CommandException("There was no player specified to invite.");
        }

        Player player = plugin.getServer().getPlayer(args.getString(0, false));
        if (player == null) {
            throw new CommandException("That player is not online.");
        }

        UUID playerId = player.getUniqueId();
        if (group.isInvited(playerId)) {
            throw new CommandException("That player is already invited.");
        }

        group.addInvitation(playerId);
        MessageBuilder.begin("Successfully invited ").highlight(player.getName()).normal(" to the ")
                .highlight(level.getName()).normal(".").send(sender);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".invite";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/" + level.getId() + " invite <player> [-g " + level.getName() + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Invites a player to the " + level.getName() + ".";
    }
}
