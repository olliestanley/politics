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
package pw.ollie.politics.group;

public final class GroupProperty {
    public static final int TAG = 0x0;
    public static final int NAME = 0x1;
    public static final int SPAWN = 0x2;
    public static final int OPEN = 0x3;
    public static final int MOTD = 0x4;
    public static final int DESCRIPTION = 0x5;
    public static final int BALANCE = 0x6;
    public static final int WAR_VICTORIES = 0x7;
    public static final int WAR_DEFEATS = 0x8;
    public static final int PEACEFUL = 0x9;
    public static final int ENTRY_MESSAGE = 0x10;
    public static final int EXIT_MESSAGE = 0x11;

    private GroupProperty() {
        throw new UnsupportedOperationException();
    }
}
