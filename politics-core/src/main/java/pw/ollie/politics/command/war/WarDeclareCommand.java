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
package pw.ollie.politics.command.war;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.PoliticsCommandHelper;
import pw.ollie.politics.command.PoliticsSubcommand;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.group.war.War;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;

public class WarDeclareCommand extends PoliticsSubcommand {
    WarDeclareCommand() {
        super("declare");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        if (args.length(false) < 1) {
            throw new CommandException("You must specify who to declare war on.");
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < args.length(false); i++) {
            nameBuilder.append(args.getString(i, false)).append(' ');
        }

        String tag = nameBuilder.toString().replace(" ", "-");
        Group target = plugin.getGroupManager().getGroupByTag(tag);
        if (target == null) {
            throw new CommandException(nameBuilder.toString() + " does not exist.");
        }

        GroupLevel level = target.getLevel();
        Group declarer = findGroup(level, sender, args);
        if (declarer.equals(target)) {
            throw new CommandException("A " + level.getName() + " cannot declare war on itself.");
        }

        if (!PoliticsCommandHelper.hasGroupsAdmin(sender) && !declarer.can(sender, Privileges.Group.DECLARE_WAR)) {
            throw new CommandException("You cannot declare a war on behalf of " + declarer.getName() + ".");
        }

        War war = new War(declarer, target);
        if (plugin.getWarManager().beginWar(war, sender)) {
            MessageBuilder.begin().highlight(declarer.getName()).normal(" has declared war on ")
                    .highlight(target.getName()).normal("!").send(sender);
        } else {
            throw new CommandException("The war could not be declared.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return "politics.war.declare";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/war declare <target> [-g declarer]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Declare war on a target organisation.";
    }
}
