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
package pw.ollie.politics.command.subplot;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.PlayerUtil;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.world.plot.Subplot;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubplotPrivilegeCommand extends SubplotSubcommand {
    SubplotPrivilegeCommand() {
        super("privilege");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Subplot subplot = findSubplot(sender, args);
        if (!hasPlotsAdmin(sender) && !subplot.can((Player) sender, Privileges.Plot.SUBPLOT_PRIVILEGES)) {
            throw new CommandException("You cannot set privileges for that subplot.");
        }

        if (args.length(false) < 3) {
            throw new CommandException("Please specify whether to add or remove a privilege, the name of the player and the name of the privilege.");
        }

        String addRemoveArg = args.getString(0, false).toLowerCase();
        boolean add;
        if (addRemoveArg.equals("add") || addRemoveArg.equals("a")) {
            add = true;
        } else if (addRemoveArg.equals("remove") || addRemoveArg.equals("r")) {
            add = false;
        } else {
            throw new CommandException(addRemoveArg + " is not either add or remove.");
        }

        String playerName = args.getString(1, false);
        OfflinePlayer player = PlayerUtil.getOfflinePlayer(playerName);
        if (player == null) {
            throw new CommandException("That player does not exist.");
        }

        String privilegeName = args.getString(2, false);
        Privilege privilege = plugin.getPrivilegeManager().getPrivilege(privilegeName.toUpperCase().replace("-", "_"));
        if (privilege == null) {
            throw new CommandException(privilegeName + " is not a privilege.");
        }

        if (add && subplot.givePrivilege(player.getUniqueId(), privilege)) {
            MessageBuilder.begin("Successfully granted privilege.").send(sender);
        } else if (!add && subplot.revokePrivilege(player.getUniqueId(), privilege)) {
            MessageBuilder.begin("Successfully revoked privilege.").send(sender);
        } else {
            throw new CommandException("Could not modify privilege status - was it already set to that value?");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return "politics.plot.subplot.privilege";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/subplot privilege <add/remove> <player> <privilege> [-p location] [-sp subplot-id]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Manage privileges for a subplot you own";
    }
}
