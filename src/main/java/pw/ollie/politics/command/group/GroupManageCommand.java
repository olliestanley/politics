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

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class GroupManageCommand extends GroupSubCommand {
    GroupManageCommand(GroupLevel groupLevel) {
        super("manage", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.MANAGE)) {
            throw new CommandException("You don't have permission to do that in your " + groupLevel.getName() + ".");
        }

        // todo list
        // if the command is run with no further args, print possible usages
        // invite subcommand - invite a group to become a subgroup
        // join subcommand - join a group as a subgroup
        // disaffiliate subcommand - quit as a subgroup
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".manage";
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " manage <invite/join/disaffiliate> [args...] [-g " + groupLevel.getName() + "]";
    }

    @Override
    public String getDescription() {
        return "Allows management of the " + groupLevel.getName() + ", such as inviting sub-" + groupLevel.getPlural() + ".";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("management");
    }
}
