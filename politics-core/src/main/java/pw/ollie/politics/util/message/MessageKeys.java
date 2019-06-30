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
package pw.ollie.politics.util.message;

// todo docs
public final class MessageKeys {
    public static final String ACTIVITY_SELECTION_FIRST_POINT_SET = "Activity.Selection.First-Point-Set";

    public static final String COMMAND_SPECIFY_PLAYER = "Command-Responses.Must-Specify-Player";
    public static final String COMMAND_NO_PERMISSION = "Command-Responses.No-Permission";
    public static final String COMMAND_PLAYER_OFFLINE = "Command-Responses.Player-Offline";

    public static final String COMMAND_GROUP_ADD_PLAYER_HAS_GROUP = "Command-Responses.Group.Add.Already-Has-Group";
    public static final String COMMAND_GROUP_ADD_NO_IMMEDIATE_MEMBERS = "Command-Responses.Group.Add.No-Immediate-Members";
    public static final String COMMAND_GROUP_ADD_DISALLOWED = "Command-Responses.Group.Add.Disallowed";
    public static final String COMMAND_GROUP_ADD_SUCCESS = "Command-Responses.Group.Add.Success";
    // todo add keys

    private MessageKeys() {
        throw new UnsupportedOperationException();
    }
}
