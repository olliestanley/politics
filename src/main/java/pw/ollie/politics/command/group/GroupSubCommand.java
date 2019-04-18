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

import pw.ollie.politics.Politics;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.PoliticsCommandHelper;
import pw.ollie.politics.command.PoliticsSubCommand;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.group.Citizen;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class GroupSubCommand extends PoliticsSubCommand {
    protected final GroupLevel groupLevel;

    protected GroupSubCommand(String name, GroupLevel groupLevel) {
        super(name);
        this.groupLevel = groupLevel;
    }

    protected String getBasePermissionNode() {
        return "politics.group." + groupLevel.getId();
    }

    protected Citizen getCitizen(Player player) {
        Universe universe = getUniverse(player);
        if (universe != null) {
            return universe.getCitizen(player.getUniqueId(), player.getName());
        }
        return null;
    }

    protected Universe getUniverse(Player player) {
        return Politics.getUniverseManager().getUniverse(player.getWorld(), groupLevel);
    }

    /**
     * Gets a Universe - first checking to see if one was specified, then falling back on the one currently relating to
     * the player if there isn't one specified.
     *
     * @param sender  the relevant source of the command
     * @param context the arguments provided
     * @return the universe relevant to the context
     */
    protected Universe findUniverse(CommandSender sender, Arguments context) throws CommandException {
        Universe universe;
        if (context.hasValueFlag("u")) {
            String universeName = context.getValueFlag("u").getStringValue();
            universe = Politics.getUniverseManager().getUniverse(universeName);
            if (universe == null) {
                throw new CommandException("There is no universe named '" + universeName + "'");
            }
            return universe;
        }

        if (sender instanceof Player) {
            universe = getUniverse((Player) sender);
            if (universe == null) {
                throw new CommandException("You aren't currently in a universe containing " + groupLevel.getPlural() + ".");
            }
        } else {
            throw new CommandException("You must specify a universe.");
        }

        return universe;
    }

    /**
     * Gets a Group - first checking to see if one was specified, then falling back on the one currently relating to
     * the player if there isn't one specified.
     *
     * @param sender  the relevant source of the command
     * @param context the arguments provided
     * @return the group relevant to the context
     */
    protected Group findGroup(CommandSender sender, Arguments context) throws CommandException {
        Universe universe = findUniverse(sender, context);
        Group group;
        if (context.hasValueFlag("g")) {
            String groupName = context.getValueFlag("g").getStringValue();
            group = universe.getFirstGroupByProperty(groupLevel, GroupProperty.TAG, groupName.toLowerCase());
            return group;
        }

        if (sender instanceof Player) {
            group = getCitizen((Player) sender).getGroup(groupLevel);
            if (group == null) {
                throw new CommandException("You aren't currently in a " + groupLevel.getName() + ".");
            }
        } else {
            throw new CommandException("You must specify a " + groupLevel.getName() + ".");
        }

        return group;
    }

    public boolean hasAdmin(CommandSender source) {
        return source instanceof ConsoleCommandSender || source.hasPermission(PoliticsCommandHelper.GROUPS_ADMIN_PERMISSION);
    }
}
