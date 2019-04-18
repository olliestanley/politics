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
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.Position;
import pw.ollie.politics.util.math.RotatedPosition;
import pw.ollie.politics.util.math.Vector2f;
import pw.ollie.politics.util.message.MessageUtil;
import pw.ollie.politics.world.plot.Plot;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GroupSetSpawnCommand extends GroupSubCommand {
    GroupSetSpawnCommand(GroupLevel groupLevel) {
        super("setspawn", groupLevel);
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        if (!group.can(sender, Privileges.Group.SET_SPAWN) && !hasAdmin(sender)) {
            throw new CommandException("You don't have permissions to set the spawn of your " + groupLevel.getName() + "!");
        }

        Player player;
        if (args.hasValueFlag("p")) {
            String playerName = args.getValueFlag("p").getStringValue();
            player = plugin.getServer().getPlayer(playerName);
            if (player == null) {
                throw new CommandException("The specified player is not online.");
            }
        } else if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            throw new CommandException("There was no player specified.");
        }

        Location location = player.getLocation();
        Position position = Position.fromLocation(location);
        RotatedPosition rotatedPosition = new RotatedPosition(position, new Vector2f(location.getPitch(), location.getYaw()));

        Plot plot = plugin.getWorldManager().getPlotAt(rotatedPosition.getPosition().toLocation());
        if (plot == null) {
            throw new CommandException("There is no plot here!");
        }
        if (!plot.isOwner(group)) {
            throw new CommandException("Sorry, the plot you are in must be owned by " + group.getStringProperty(GroupProperty.NAME)
                    + " to set your spawn in it!");
        }

        group.setProperty(GroupProperty.SPAWN, rotatedPosition);
        MessageUtil.message(sender, "The spawn of your " + groupLevel.getName() + " was set successfully!");
    }

    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".setspawn";
    }

    @Override
    public String getUsage() {
        return "/" + groupLevel.getId() + " setspawn [-p player] [-g " + groupLevel.getName() + "] [-u universe]";
    }

    @Override
    public String getDescription() {
        return "Sets the spawn location for a " + groupLevel.getName() + ".";
    }
}
