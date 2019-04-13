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
import pw.ollie.politics.group.level.Role;
import pw.ollie.politics.group.level.RoleTrack;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GroupPromoteCommand extends GroupSubCommand {
    GroupPromoteCommand(GroupLevel groupLevel) {
        super("promote", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (args.length(false) < 1) {
            throw new CommandException("There was no player specified to promote.");
        }

        Player player = plugin.getServer().getPlayer(args.getString(0, false));
        if (player == null) {
            // todo: promoting offline players? may be necessary for some servers
            throw new CommandException("That player is not online!");
        }
        if (!group.isImmediateMember(player.getUniqueId())) {
            throw new CommandException("That player is not a member of the group!");
        }

        RoleTrack track;
        String trackName = null;
        if (args.hasValueFlag("t")) {
            trackName = args.getValueFlag("t").getStringValue();
            track = group.getLevel().getTrack(args.getValueFlag("t").getStringValue().toLowerCase());
        } else {
            track = group.getLevel().getDefaultTrack();
        }
        if (track == null) {
            throw new CommandException("There isn't a track named '" + trackName + "'!");
        }

        Role role = group.getRole(player.getUniqueId());
        Role next = track.getNextRole(role);
        if (next == null) {
            throw new CommandException("There is no role to promote to!");
        }

        if (!hasAdmin(sender)) {
            // cast is safe as a console sender is filtered out by the hasAdmin check
            Role myRole = group.getRole(((Player) sender).getUniqueId());
            if (myRole.getRank() - next.getRank() <= 1) {
                throw new CommandException("You can't promote someone to a role equal to or higher than your own!");
            }
        }

        group.setRole(player.getUniqueId(), next);
        sender.sendMessage(player.getName() + " was promoted to " + next.getName() + " in the group.");
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".promote";
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " promote <player> [-t track] [-g " + groupLevel.getName() + "] [-u universe]";
    }

    @Override
    public String getDescription() {
        return "Promotes somebody in a " + groupLevel.getName() + ".";
    }
}
