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
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class GroupDestroyCommand extends GroupSubcommand {
    GroupDestroyCommand(GroupLevel groupLevel) {
        super("destroy", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.DISBAND) && !hasAdmin(sender)) {
            throw new CommandException("You aren't allowed to disband this group!");
        }

        group.getUniverse().destroyGroup(group);
        MessageBuilder.begin("The " + level.getName() + " ").highlight(group.getName())
                .normal(" has been disbanded.").send(sender);
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".destroy";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("delete", "remove");
    }

    @Override
    public String getUsage() {
        return "/" + level.getId() + " destroy <group>";
    }

    @Override
    public String getDescription() {
        return "Destroys a " + level.getName() + ".";
    }
}
