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
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GroupManageCommand extends GroupSubcommand {
    private final Map<String, GroupManageSubCommand> subcommands;

    GroupManageCommand(GroupLevel groupLevel) {
        super("manage", groupLevel);
        this.subcommands = new THashMap<>();

        this.registerSubCommand(new GroupManageInviteCommand());
        this.registerSubCommand(new GroupManageJoinCommand());
        this.registerSubCommand(new GroupManageDisaffiliateCommand());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.MANAGE)) {
            throw new CommandException("You don't have permission to do that in your " + level.getName() + ".");
        }

        if (args.length(false) < 1) {
            throw new CommandException("There was no subcommand specified (invite/join/disaffiliate).");
        }

        String subcommand = args.getString(0, false);
        GroupManageSubCommand subcommandExecutor = subcommands.get(subcommand);
        if (subcommandExecutor == null) {
            throw new CommandException("The specified subcommand is invalid (invite/join/disaffiliate).");
        }

        subcommandExecutor.runCommand(plugin, sender, args.subArgs(1, args.length()), group);
    }

    private void registerSubCommand(GroupManageSubCommand subcommand) {
        subcommands.put(subcommand.getName(), subcommand);
    }

    private abstract class GroupManageSubCommand {
        private final String name;

        private GroupManageSubCommand(String name) {
            this.name = name;
        }

        protected abstract void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args, Group group) throws CommandException;

        String getName() {
            return name;
        }
    }

    private class GroupManageInviteCommand extends GroupManageSubCommand {
        private GroupManageInviteCommand() {
            super("invite");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args, Group group) throws CommandException {
            if (args.length() < 1) {
                throw new CommandException("There was no " + level.getName() + " specified to invite.");
            }

            String invitedGroupTag = args.getString(0, false);
            Optional<Group> lookup = plugin.getGroupManager().getGroupByTag(invitedGroupTag);
            if (!lookup.isPresent()) {
                throw new CommandException("No " + level.getName() + " with that tag exists.");
            }

            Group invited = lookup.get();
            Player player = (Player) sender;
            Universe universe = plugin.getUniverseManager().getUniverse(player.getWorld(), level).orElse(null);
            if (universe == null || !universe.hasGroup(invited)) {
                throw new CommandException(invited.getName() + " does not exist in the same universe as " + level.getPlural() + ".");
            }
            if (!(group.getLevel().getRank() > invited.getLevel().getRank())) {
                throw new CommandException("A " + group.getLevel().getName() + " cannot have " + invited.getLevel().getPlural() + " as sub-organisations.");
            }

            if (group.inviteChild(invited, sender)) {
                MessageBuilder.begin("The ").highlight(level.getName()).normal(" was successfully invited.").send(sender);
            } else {
                throw new CommandException("That " + level.getName() + " cannot be invited as a child of yours.");
            }
        }
    }

    private class GroupManageJoinCommand extends GroupManageSubCommand {
        private GroupManageJoinCommand() {
            super("join");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args, Group group) throws CommandException {
            if (args.length() < 1) {
                throw new CommandException("There was no " + level.getName() + " specified to join.");
            }

            if (group.getParent().isPresent()) {
                throw new CommandException(group.getName() + " already has a parent organisation.");
            }

            String parentTag = args.getString(0, false);
            Optional<Group> lookup = plugin.getGroupManager().getGroupByTag(parentTag);
            if (!lookup.isPresent()) {
                throw new CommandException("No " + level.getName() + " with that tag exists.");
            }

            Group parent = lookup.get();
            if (parent.isInvitedChild(group)) {
                throw new CommandException(parent.getName() + " has not invited " + group.getName() + " to be a sub-organisation.");
            }

            if (parent.addChild(group)) {
                parent.disinviteChild(group);
                MessageBuilder.begin().highlight(parent.getName()).normal(" has successfully joined ")
                        .highlight(group.getName()).normal(" as a sub-organisation.").send(sender);
            } else {
                throw new CommandException("Your " + level.getName() + " cannot be a child of that " + parent.getLevel().getName());
            }
        }
    }

    private class GroupManageDisaffiliateCommand extends GroupManageSubCommand {
        private GroupManageDisaffiliateCommand() {
            super("disaffiliate");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args, Group group) throws CommandException {
            if (args.length() < 1) {
                throw new CommandException("There was no " + level.getName() + " specified to join.");
            }

            String otherTag = args.getString(0, false);
            Optional<Group> lookup = plugin.getGroupManager().getGroupByTag(otherTag);
            if (!lookup.isPresent()) {
                throw new CommandException("No " + level.getName() + " with that tag exists.");
            }

            Group other = lookup.get();
            if (group.equals(other.getParent().orElse(null))) {
                if (group.removeChild(other, sender)) {
                    MessageBuilder.begin().highlight(group.getName() + " is no longer the parent organisation of ")
                            .highlight(other.getName()).normal(".").send(sender);
                } else {
                    throw new CommandException(group.getName() + " cannot disaffiliate from " + other.getName() + ".");
                }
                return;
            }

            if (other.equals(group.getParent().orElse(null))) {
                if (other.removeChild(group, sender)) {
                    MessageBuilder.begin().highlight(group.getName() + " is no longer the child organisation of ")
                            .highlight(other.getName()).normal(".").send(sender);
                } else {
                    throw new CommandException(group.getName() + " cannot disaffiliate from " + other.getName() + ".");
                }
                return;
            }

            throw new CommandException(group.getName() + " is not affiliated with " + other.getName() + ".");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".manage";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/" + level.getId() + " manage <invite/join/disaffiliate> [args...] [-g " + level.getName() + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Allows management of the " + level.getName() + ", such as inviting sub-" + level.getPlural() + ".";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("management");
    }
}
