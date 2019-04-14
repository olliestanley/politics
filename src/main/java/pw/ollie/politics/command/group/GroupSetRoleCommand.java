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
import pw.ollie.politics.event.group.GroupMemberRoleChangeEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.level.Role;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GroupSetRoleCommand extends GroupSubCommand {
    GroupSetRoleCommand(GroupLevel groupLevel) {
        super("setrole", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.SET_ROLE)) {
            throw new CommandException("You don't have permission to set player roles.");
        }

        if (args.length(false) < 2) {
            throw new CommandException("There must be both a player and role specified");
        }

        Player player = plugin.getServer().getPlayer(args.getString(0));
        if (player == null) {
            // todo setting roles of offline players?
            throw new CommandException("That player is not online!");
        }
        if (!group.isImmediateMember(player.getUniqueId())) {
            throw new CommandException("That player is not a member of the group!");
        }

        String rn = args.getString(1);
        Role role = group.getLevel().getRole(rn);
        if (role == null) {
            throw new CommandException("There isn't a role named `" + rn + "'!");
        }

        if (!hasAdmin(sender)) {
            Role myRole = group.getRole(((Player) sender).getUniqueId());
            if (myRole.getRank() - role.getRank() <= 1) {
                throw new CommandException("You can't set someone to a role equal to or higher than your own!");
            }
        }

        Role oldRole = group.getRole(player.getUniqueId());
        GroupMemberRoleChangeEvent roleChangeEvent = plugin.getEventFactory().callGroupMemberRoleChangeEvent(group, player, oldRole, role);
        if (roleChangeEvent.isCancelled()) {
            throw new CommandException("You can't set that player's role,");
        }

        group.setRole(player.getUniqueId(), role);
        MessageBuilder.begin().highlight(player.getName()).normal(" had their role set to ").highlight(role.getName())
                .normal(" in the ").append(groupLevel.getName()).append("!").send(sender);
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".setrole";
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " setrole <player> <role> [-g group] [-u universe]";
    }

    @Override
    public String getDescription() {
        return "Sets somebody's role in a " + groupLevel.getName() + ".";
    }
}
