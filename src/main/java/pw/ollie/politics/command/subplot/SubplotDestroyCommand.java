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
import pw.ollie.politics.command.PoliticsCommandHelper;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.Subplot;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class SubplotDestroyCommand extends SubplotSubCommand {
    SubplotDestroyCommand() {
        super("destroy");
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        if (args.length(false) < 1) {
            throw new CommandException("There was no specified subplot to destroy.");
        }

        Subplot subplot = findSubplot(sender, args.getString(0, false), args);
        Plot parentPlot = subplot.getParent();
        Group plotOwner = parentPlot.getOwner();
        if ((plotOwner == null || !plotOwner.can(sender, Privileges.GroupPlot.MANAGE_SUBPLOTS))
                && !sender.hasPermission(PoliticsCommandHelper.PLOTS_ADMIN_PERMISSION)) {
            throw new CommandException("You can't do that.");
        }

        if (parentPlot.removeSubplot(subplot)) {
            MessageBuilder.begin("Successfully removed subplot.").send(sender);
        } else {
            throw new CommandException("You can't remove that subplot.");
        }
    }

    @Override
    public String getPermission() {
        return "politics.plot.subplot.destroy";
    }

    @Override
    public String getUsage() {
        return "/subplot destroy <here> OR <id> OR <x,y,z> [-p plot-location]";
    }

    @Override
    public String getDescription() {
        return "Removes a subplot from a plot";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("delete", "remove");
    }
}
