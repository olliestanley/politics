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
package pw.ollie.politics.event.plot;

import pw.ollie.politics.Politics;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.world.plot.Plot;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlotOwnerChangeEvent extends PlotEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final int group;
    private final boolean add;

    private boolean cancelled;

    public PlotOwnerChangeEvent(final Plot plot, final int group, final boolean add) {
        super(plot);
        this.group = group;
        this.add = add;
    }

    public int getGroupId() {
        return group;
    }

    public Group getGroup() {
        return Politics.getGroupById(group);
    }

    public boolean isAdd() {
        return add;
    }

    public boolean isRemove() {
        return !add;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
