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
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GroupCreateCommand extends GroupSubCommand {
    GroupCreateCommand(GroupLevel groupLevel) {
        super("create", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        String founderName = null;
        if (sender instanceof Player) {
            founderName = sender.getName();
        }

        if (hasAdmin(sender) && args.hasValueFlag("f")) {
            founderName = args.getValueFlag("f").getStringValue();
        }

        // Check for a founder, this would only happen if he is not a player
        if (founderName == null) {
            throw new CommandException("The founder for the to-be-created " + groupLevel.getName()
                    + " is unknown. A founder can be specified with the `-f' option.");
        }

        Player founder = plugin.getServer().getPlayer(founderName);
        if (founder == null) {
            throw new CommandException("The specified founder is not online.");
        }

        Universe universe = findUniverse(sender, args);

        if (!groupLevel.allowedMultiple()) {
            Set<Group> founderGroups = universe.getCitizenGroups(founder);
            for (Group group : founderGroups) {
                if (group.getLevel().equals(groupLevel)) {
                    throw new CommandException("A " + groupLevel.getName() + " may not be founded by somebody already in one.");
                }
            }
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < args.length(); i++) {
            nameBuilder.append(args.getString(i)).append(' ');
        }

        String name = nameBuilder.toString().trim();
        String tag = name.toLowerCase().replace(" ", "-");
        if (args.hasValueFlag("t")) {
            tag = args.getValueFlag("t").getStringValue();
        }

        Group group = universe.createGroup(groupLevel);
        group.setRole(founder.getUniqueId(), groupLevel.getFounder());
        group.setProperty(GroupProperty.NAME, name);
        group.setProperty(GroupProperty.TAG, tag);

        if (PoliticsEventFactory.callGroupCreateEvent(group, sender).isCancelled()) {
            universe.destroyGroup(group);
            throw new CommandException(groupLevel.getName() + " creation denied!");
        }

        MessageBuilder.begin("Your ").highlight(groupLevel.getName()).normal(" was successfully created.").send(sender);
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".create";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("new", "setup");
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " create <name> [-f founder] [-u universe] [-t tag]";
    }

    @Override
    public String getDescription() {
        return "Creates a new " + groupLevel.getName() + ".";
    }
}
