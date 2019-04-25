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
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.PlayerUtil;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.world.plot.Subplot;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class SubplotSetownerCommand extends SubplotSubcommand {
    SubplotSetownerCommand() {
        super("setowner");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Subplot subplot = findSubplot(sender, args);
        Group group = subplot.getParent().getOwner();
        if (!hasPlotsAdmin(sender) && !group.can(sender, Privileges.GroupPlot.MANAGE_SUBPLOTS)) {
            throw new CommandException("You cannot set the owner for that subplot.");
        }

        if (args.length(false) < 1) {
            throw new CommandException("Please specify he name of the player to designate as owner.");
        }

        String playerName = args.getString(1, false);
        OfflinePlayer player = PlayerUtil.getOfflinePlayer(playerName);
        if (player == null) {
            throw new CommandException("That player does not exist.");
        }

        if (subplot.setOwner(player.getUniqueId())) {
            MessageBuilder.begin("Successfully set owner.").send(sender);
        } else {
            throw new CommandException("Cannot set owner of subplot.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return "politics.plot.subplot.setowner";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/subplot setowner <player> [-p location] [-sp subplot-id]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Set the owner of a subplot";
    }
}
