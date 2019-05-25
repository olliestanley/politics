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
package pw.ollie.politicswar.util;

import pw.ollie.politicswar.event.war.WarBeginEvent;
import pw.ollie.politicswar.event.war.WarFinishEvent;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PoliticsWarEventCounter implements Listener {
    private int warBegin = 0;
    private int warEnd = 0;

    @EventHandler
    public void event(Event event) {
        if (event instanceof WarBeginEvent) {
            warBegin++;
        } else if (event instanceof WarFinishEvent) {
            warEnd++;
        }
    }

    public int getWarBegin() {
        return warBegin;
    }

    public int getWarEnd() {
        return warEnd;
    }
}
