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
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.util.message.MessageUtil;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class GroupInfoCommand extends GroupSubcommand {
    GroupInfoCommand(GroupLevel groupLevel) {
        super("info", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        MessageBuilder message = MessageUtil.startBlockMessage(level.getName() + " Info");
        message.newLine().normal("Name: ").highlight(group.getName());
        message.newLine().normal("Tag: ").highlight(group.getTag());
        message.newLine().normal("Description: ").highlight(group.getStringProperty(GroupProperty.DESCRIPTION));
        message.newLine().normal("Members: ").highlight(Integer.toString(group.getPlayers().size()));
        message.newLine().normal("Open: ").highlight(group.getBooleanProperty(GroupProperty.OPEN, false) ? "Yes" : "No");
        message.send(sender);
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".info";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("about");
    }

    @Override
    public String getUsage() {
        return "/" + level.getId() + " info [-g " + level.getName() + "]";
    }

    @Override
    public String getDescription() {
        return "Provides information about a " + level.getName() + ".";
    }
}
