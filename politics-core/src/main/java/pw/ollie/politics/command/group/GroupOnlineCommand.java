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
import pw.ollie.politics.command.args.Argument;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.util.collect.PagedArrayList;
import pw.ollie.politics.util.collect.PagedList;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.util.message.MessageUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GroupOnlineCommand extends GroupSubcommand {
    GroupOnlineCommand(GroupLevel groupLevel) {
        super("online", groupLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Group group = findGroup(sender, args);

        int pageNo = 1;
        if (args.length(false) > 0) {
            Argument arg = args.get(0, false);
            if (arg.isInt()) {
                pageNo = arg.asInt();
            }
        }

        List<Player> online = group.getImmediateOnlinePlayers();
        PagedList<Player> paged = new PagedArrayList<>(online);
        paged.setElementsPerPage(25); // todo make configurable?
        if (pageNo > paged.pages()) {
            throw new CommandException("There are only " + paged.pages() + " pages!");
        }

        List<Player> page = paged.getPage(pageNo);
        MessageBuilder message = MessageUtil.startBlockMessage(group.getName() + " - Online Players (" + pageNo + " of " + paged.pages() + ")")
                .newLine();
        for (int i = 0; i < page.size(); i++) {
            message.append(page.get(i).getName());

            if (i < page.size() - 1) {
                message.append(", ");
            }
        }
        message.send(sender);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return getBasePermissionNode() + ".online";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/" + level.getId() + " online [-g " + level.getName() + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Lists online players in the " + level.getName() + ".";
    }
}
