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

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class GroupInfoCommand extends GroupSubCommand {
    GroupInfoCommand(GroupLevel groupLevel) {
        super("info", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        // todo more info than just the name
        sender.sendMessage("============= INFO =============");
        sender.sendMessage("Current Group: " + group.getStringProperty(GroupProperty.NAME));
        sender.sendMessage("================================");
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
        return "/" + groupLevel.getId() + " info [-g " + groupLevel.getName() + "]";
    }

    @Override
    public String getDescription() {
        return "Provides information about a " + groupLevel.getName() + ".";
    }
}
