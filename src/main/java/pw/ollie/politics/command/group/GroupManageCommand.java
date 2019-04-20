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

import gnu.trove.map.hash.THashMap;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupAffiliationRequest;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GroupManageCommand extends GroupSubCommand {
    private final Map<String, GroupManageSubCommand> subCommands;

    GroupManageCommand(GroupLevel groupLevel) {
        super("manage", groupLevel);
        this.subCommands = new THashMap<>();

        this.registerSubCommand(new GroupManageInviteCommand());
        this.registerSubCommand(new GroupManageJoinCommand());
        this.registerSubCommand(new GroupManageDisaffiliateCommand());
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.MANAGE)) {
            throw new CommandException("You don't have permission to do that in your " + groupLevel.getName() + ".");
        }

        if (args.length(false) < 1) {
            throw new CommandException("There was no subcommand specified (invite/join/disaffiliate).");
        }

        String subCommand = args.getString(0, false);
        GroupManageSubCommand subCommandExecutor = subCommands.get(subCommand);
        if (subCommandExecutor == null) {
            throw new CommandException("The specified subcommand is invalid (invite/join/disaffiliate).");
        }

        subCommandExecutor.runCommand(plugin, sender, args.subArgs(1, args.length()));
    }

    private void registerSubCommand(GroupManageSubCommand subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
    }

    private abstract class GroupManageSubCommand {
        private final String name;

        private GroupManageSubCommand(String name) {
            this.name = name;
        }

        public abstract void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException;

        public String getName() {
            return name;
        }
    }

    private class GroupManageInviteCommand extends GroupManageSubCommand {
        private GroupManageInviteCommand() {
            super("invite");
        }

        @Override
        public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
            if (args.length() < 1) {
                throw new CommandException("There was no " + groupLevel.getName() + " specified to invite.");
            }

            String invitedGroupTag = args.getString(0, false);
            Group invited = plugin.getGroupManager().getGroupByTag(invitedGroupTag);
            if (invited == null) {
                throw new CommandException("No " + groupLevel.getName() + " with that tag exists.");
            }

            Player player = (Player) sender;
            Group group = findGroup(sender, args);
            Universe universe = plugin.getUniverseManager().getUniverse(player.getWorld(), groupLevel);
            if (!universe.getGroups().contains(invited)) {
                throw new CommandException(invited.getStringProperty(GroupProperty.NAME) + " does not exist in the same universe as " + groupLevel.getPlural() + ".");
            }
            if (!(group.getLevel().getRank() > invited.getLevel().getRank())) {
                throw new CommandException("A " + group.getLevel().getName() + " cannot have " + invited.getLevel().getPlural() + " as sub-organisations.");
            }

            GroupAffiliationRequest affiliationRequest = new GroupAffiliationRequest(group.getUid(), invited.getUid());
            if (plugin.getGroupManager().addAffiliationRequest(affiliationRequest)) {
                MessageBuilder.begin("The ").highlight(groupLevel.getName()).normal(" was successfully invited.").send(sender);
            } else {
                throw new CommandException("That " + groupLevel.getName() + " cannot be invited as a child of yours.");
            }
        }
    }

    private class GroupManageJoinCommand extends GroupManageSubCommand {
        private GroupManageJoinCommand() {
            super("join");
        }

        @Override
        public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
            // todo: accept an invitation to become a child of another group
            // usage: /*g* manage join <newparent> [-g group]
        }
    }

    private class GroupManageDisaffiliateCommand extends GroupManageSubCommand {
        private GroupManageDisaffiliateCommand() {
            super("disaffiliate");
        }

        @Override
        public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
            // todo: disaffiliate from another group - either parent or child
            // usage: /*g* manage disaffiliate <othergroup> [-g group]
        }
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
