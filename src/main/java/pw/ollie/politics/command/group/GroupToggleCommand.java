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
import pw.ollie.politics.group.GroupToggleables;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;

public class GroupToggleCommand extends GroupSubCommand {
    GroupToggleCommand(GroupLevel groupLevel) {
        super("toggle", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.TOGGLES) && !hasAdmin(sender)) {
            throw new CommandException("You aren't allowed to toggle that " + groupLevel.getName() + "'s settings.");
        }

        if (args.length(false) < 1) {
            throw new CommandException("There wasn't a toggle name specified");
        }

        String toggleName = args.getString(0, false);
        if (!GroupToggleables.isToggleableProperty(toggleName)) {
            throw new CommandException("The provided toggle name was not a valid toggle!");
        }

        int propertyId = GroupToggleables.getPropertyId(toggleName);
        boolean curValue = group.getBooleanProperty(propertyId);
        group.setProperty(propertyId, !curValue);
        MessageBuilder.begin("State of ").highlight(toggleName).normal(" switched to: ")
                .highlight(Boolean.toString(!curValue)).normal(".").send(sender);
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".toggle";
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " toggle <toggle> [-g" + groupLevel.getName() + "]";
    }

    @Override
    public String getDescription() {
        return "Toggle settings for the " + groupLevel.getName() + ".";
    }
}
