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
package net.oliverstanley.politics.event.group;

import net.oliverstanley.politics.event.Sourced;
import net.oliverstanley.politics.group.Group;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class GroupChildInviteEvent extends GroupEvent implements Sourced {
    private static final HandlerList handlers = new HandlerList();

    private final Group child;
    private final CommandSender source;

    public GroupChildInviteEvent(Group group, Group child, CommandSender source) {
        super(group);
        this.child = child;
        this.source = source;
    }

    public Group getChild() {
        return child;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandSender getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
