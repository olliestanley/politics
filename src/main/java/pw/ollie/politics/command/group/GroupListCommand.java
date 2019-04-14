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
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.util.message.MessageUtil;

import org.bukkit.command.CommandSender;

import java.util.List;

public class GroupListCommand extends GroupSubCommand {
    public static final int PAGE_HEIGHT = 6;

    GroupListCommand(GroupLevel groupLevel) {
        super("list", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Universe universe = findUniverse(sender, args);

        List<Group> groups = universe.getGroups(groupLevel);
        if (groups.isEmpty()) {
            throw new CommandException("There are no " + groupLevel.getPlural() + "!");
        }

        int page = 1;
        if (args.length() > 0) {
            Argument arg1 = args.get(0);
            if (!arg1.isInt()) {
                throw new CommandException("Invalid page number supplied!");
            }

            page = arg1.asInt();
        }

        int min = (page - 1) * PAGE_HEIGHT - 1; // Screen height
        int max = Math.min(groups.size(), page * PAGE_HEIGHT) - 2;
        if (max <= min) {
            throw new CommandException("There are no " + groupLevel.getPlural() + " on this page!");
        }

        MessageBuilder message = MessageUtil.startBlockMessage(groupLevel.getPlural().toUpperCase());

        List<Group> pageGroups = groups.subList(min, max);
        for (Group group : pageGroups) {
            message.newLine().highlight().append((String) group.getProperty(GroupProperty.TAG));
        }

        message.build().send(sender);
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".list";
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " list [page]";
    }

    @Override
    public String getDescription() {
        return "Provides a list of " + groupLevel.getPlural() + ".";
    }
}
