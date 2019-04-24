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
import pw.ollie.politics.command.args.Argument;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.message.MessageUtil;

import org.bukkit.command.CommandSender;

public class GroupSettaxCommand extends GroupSubcommand {
    GroupSettaxCommand(GroupLevel groupLevel) {
        super("settax", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (args.length(false) > 0) {
            if (!group.can(sender, Privileges.Group.SET_TAX) && !hasAdmin(sender)) {
                throw new CommandException("You don't have permissions to set the tax of your " + level.getName() + "!");
            }

            Argument taxArg = args.get(0, false);
            if (!taxArg.isDouble()) {
                throw new CommandException(taxArg.toString() + " is not a valid tax amount.");
            }

            double amount = taxArg.asDouble();
            if (amount > plugin.getPoliticsConfig().getMaxFixedTax()) {
                throw new CommandException("Tha maximum tax is " + plugin.getPoliticsConfig().getMaxFixedTax() + ".");
            }

            group.setProperty(GroupProperty.FIXED_TAX, amount);
            MessageUtil.message(sender, "The tax of your " + level.getName() + " was set successfully!");
            return;
        }

        throw new CommandException("You must specify a new amount to set the tax to.");
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".settax";
    }

    @Override
    public String getUsage() {
        return "/" + level.getId() + " settax [-g " + level.getName() + "]";
    }

    @Override
    public String getDescription() {
        return "View or set the tax for a " + level.getName() + ".";
    }
}
