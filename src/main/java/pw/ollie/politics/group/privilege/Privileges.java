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
package pw.ollie.politics.group.privilege;

import java.util.Arrays;
import java.util.List;

public final class Privileges {
    public static final class Group {
        public static final Privilege CLAIM = new Privilege("CLAIM", PrivilegeType.GROUP);
        public static final Privilege DISBAND = new Privilege("DISBAND", PrivilegeType.GROUP);
        public static final Privilege INFO = new Privilege("INFO", PrivilegeType.GROUP);
        public static final Privilege INVITE = new Privilege("INVITE", PrivilegeType.GROUP);
        public static final Privilege KICK = new Privilege("KICK", PrivilegeType.GROUP);
        public static final Privilege LEAVE = new Privilege("LEAVE", PrivilegeType.GROUP);
        public static final Privilege MANAGE = new Privilege("MANAGE", PrivilegeType.GROUP);
        public static final Privilege ONLINE = new Privilege("ONLINE", PrivilegeType.GROUP);
        public static final Privilege SET_ROLE = new Privilege("SET_ROLE", PrivilegeType.GROUP);
        public static final Privilege SPAWN = new Privilege("SPAWN", PrivilegeType.GROUP);
        public static final Privilege SET_SPAWN = new Privilege("SET_SPAWN", PrivilegeType.GROUP);
        public static final Privilege SPAWN_OTHER = new Privilege("SPAWN_OTHER", PrivilegeType.GROUP);
        public static final Privilege TOGGLES = new Privilege("TOGGLES", PrivilegeType.GROUP);
        public static final Privilege UNCLAIM = new Privilege("UNCLAIM", PrivilegeType.GROUP);
        public static final Privilege[] ALL = {CLAIM, DISBAND, INFO, INVITE, KICK, LEAVE, MANAGE, ONLINE, SET_ROLE, SET_SPAWN, SPAWN, SPAWN_OTHER, TOGGLES, UNCLAIM};

        public static List<Privilege> all() {
            return Arrays.asList(ALL);
        }
    }

    public static final class Plot {
        public static final Privilege SUBPLOT_PRIVILEGES = new Privilege("SUBPLOT_PRIVILEGES", PrivilegeType.PLOT);
        public static final Privilege[] ALL = {SUBPLOT_PRIVILEGES};

        public static List<Privilege> all() {
            return Arrays.asList(ALL);
        }
    }

    public static final class GroupPlot {
        public static final Privilege BUILD = new Privilege("BUILD", PrivilegeType.GROUP, PrivilegeType.PLOT);
        public static final Privilege INTERACT = new Privilege("INTERACT", PrivilegeType.GROUP, PrivilegeType.PLOT);
        public static final Privilege MANAGE_SUBPLOTS = new Privilege("MANAGE_SUBPLOTS", PrivilegeType.GROUP, PrivilegeType.PLOT);
        public static final Privilege[] ALL = {BUILD, INTERACT, MANAGE_SUBPLOTS};

        public static List<Privilege> all() {
            return Arrays.asList(ALL);
        }
    }

    public static final Privilege[] ALL = {
            Group.CLAIM, Group.DISBAND, Group.INFO, Group.LEAVE, Group.MANAGE, Group.ONLINE, Group.INVITE, Group.KICK,
            Group.SET_ROLE, Group.SPAWN, Group.SET_SPAWN, Group.SPAWN_OTHER, Group.TOGGLES, Group.UNCLAIM,
            Plot.SUBPLOT_PRIVILEGES,
            GroupPlot.BUILD, GroupPlot.INTERACT, GroupPlot.MANAGE_SUBPLOTS
    };

    public static List<Privilege> all() {
        return Arrays.asList(ALL);
    }

    private Privileges() {
        throw new UnsupportedOperationException();
    }
}
