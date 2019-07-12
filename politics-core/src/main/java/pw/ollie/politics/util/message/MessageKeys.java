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
    // todo defaults
    public static final String COMMAND_BAD_PAGE = "Command-Responses.Invalid-Page-Number";
    public static final String COMMAND_PAGE_NOT_EXISTS = "Command-Responses.Page-Not-Exists";
    public static final String COMMAND_BAD_PLAYER = "Command-Responses.Invalid-Player";
    public static final String COMMAND_NO_PLOTS = "Command-Responses.No-Plots";
    public static final String COMMAND_NO_PLOT_AT_LOCATION = "Command-Responses.No-Plot-There";

    public static final String COMMAND_GROUP_NO_PRIVILEGE = "Command-Responses.Group.No-Privilege";
    public static final String COMMAND_GROUP_BAD_WORLD = "Command-Responses.Group.Invalid-World";
    public static final String COMMAND_GROUP_PLAYER_NOT_MEMBER = "Command-Responses.Group.Player-Not-Member";
    public static final String COMMAND_GROUP_TRACK_BAD_TRACK = "Command-Responses.Group.Invalid-Track";
    public static final String COMMAND_GROUP_BAD_GROUP = "Command-Responses.Group.Invalid-Group";
    public static final String COMMAND_GROUP_NOT_MEMBER = "Command-Responses.Group.Not-Member";
    public static final String COMMAND_GROUP_BAD_ROLE = "Command-Responses.Group.Bad-Role";
    // end

    public static final String COMMAND_GROUP_ADD_PLAYER_HAS_GROUP = "Command-Responses.Group.Add.Already-Has-Group";
    public static final String COMMAND_GROUP_ADD_NO_IMMEDIATE_MEMBERS = "Command-Responses.Group.Add.No-Immediate-Members";
    public static final String COMMAND_GROUP_ADD_DISALLOWED = "Command-Responses.Group.Add.Disallowed";
    public static final String COMMAND_GROUP_ADD_SUCCESS = "Command-Responses.Group.Add.Success";

    // todo defaults
    public static final String COMMAND_GROUP_CLAIM_CANNOT_OWN = "Command-Responses.Group.Claim.Cannot-Own";
    public static final String COMMAND_GROUP_CLAIM_ALREADY_OWNED = "Command-Responses.Group.Claim-Already-Owned";
    public static final String COMMAND_GROUP_CLAIM_DISALLOWED = "Command-Responses.Group.Claim.Disallowed";
    public static final String COMMAND_GROUP_CLAIM_SUCCESS = "Command-Responses.Group.Claim.Success";

    public static final String COMMAND_GROUP_CREATE_SPECIFY_FOUNDER = "Command-Responses.Group.Create.Specify-Founder";
    public static final String COMMAND_GROUP_CREATE_SPECIFY_NAME = "Command-Responses.Group.Create.Specify-Name";
    public static final String COMMAND_GROUP_CREATE_ALREADY_MEMBER = "Command-Responses.Group.Create.Already-Member";
    public static final String COMMAND_GROUP_CREATE_NAME_TAKEN = "Command-Responses.Group.Create.Name-Taken";
    public static final String COMMAND_GROUP_CREATE_DISALLOWED = "Command-Responses.Group.Create.Disallowed";
    public static final String COMMAND_GROUP_CREATE_SUCCESS = "Command-Responses.Group.Create.Success";

    public static final String COMMAND_GROUP_DEMOTE_HIGHER_RANK = "Command-Responses.Group.Demote.Higher-Rank";
    public static final String COMMAND_GROUP_DEMOTE_SPECIFY_PLAYER = "Command-Responses.Group.Demote.Specify-Player";
    public static final String COMMAND_GROUP_DEMOTE_NO_ROLE = "Command-Responses.Group.Demote.No-Role";
    public static final String COMMAND_GROUP_DEMOTE_DISALLOWED = "Command-Responses.Group.Demote.Disallowed";
    public static final String COMMAND_GROUP_DEMOTE_SUCCESS = "Command-Responses.Group.Demote.Success";

    public static final String COMMAND_GROUP_DESCRIPTION_NO_DESCRIPTION = "Command-Responses.Group.Description.No-Description";
    public static final String COMMAND_GROUP_DESCRIPTION_SET_SUCCESS = "Command-Responses.Group.Description.Set-Success";

    public static final String COMMAND_GROUP_DESTROY_SUCCESS = "Command-Responses.Group.Destroy.Success";

    public static final String COMMAND_GROUP_ENTRY_NO_MESSAGE = "Command-Responses.Group.Entry.No-Message";
    public static final String COMMAND_GROUP_ENTRY_SET_SUCCESS = "Command-Responses.Group.Entry.Set-Success";

    public static final String COMMAND_GROUP_EXIT_NO_MESSAGE = "Command-Responses.Group.Exit.No-Message";
    public static final String COMMAND_GROUP_EXIT_SET_SUCCESS = "Command-Responses.Group.Exit.Set-Success";

    public static final String COMMAND_GROUP_INVITE_NO_IMMEDIATE_MEMBERS = "Command-Responses.Group.Invite.No-Immediate-Members";
    public static final String COMMAND_GROUP_INVITE_OPEN_GROUP = "Command-Responses.Group.Invite.Open-Group";
    public static final String COMMAND_GROUP_INVITE_ALREADY_INVITED = "Command-Responses.Group.Invite.Already-Invited";
    public static final String COMMAND_GROUP_INVITE_SUCCESS = "Command-Responses.Group.Invite.Success";

    public static final String COMMAND_GROUP_JOIN_NO_IMMEDIATE_MEMBERS = "Command-Responses.Group.Join.No-Immediate-Members";
    public static final String COMMAND_GROUP_JOIN_SPECIFY_PLAYER = "Command-Responses.Group.Join.Specify-Player";
    public static final String COMMAND_GROUP_JOIN_ALREADY_MEMBER = "Command-Responses.Group.Join.Already-Member";
    public static final String COMMAND_GROUP_JOIN_NOT_INVITED = "Command-Responses.Group.Join.Not-Invited";
    public static final String COMMAND_GROUP_JOIN_DISALLOWED = "Command-Responses.Group.Join.Disallowed";
    public static final String COMMAND_GROUP_JOIN_SUCCESS = "Command-Responses.Group.Join.Success";

    public static final String COMMAND_GROUP_KICK_SPECIFY_PLAYER = "Command-Responses.Group.Kick.Specify-Player";
    public static final String COMMAND_GROUP_KICK_DISALLOWED = "Command-Responses.Group.Kick.Disallowed";
    public static final String COMMAND_GROUP_KICK_SUCCESS = "Command-Responses.Group.Kick.Success";

    public static final String COMMAND_GROUP_LEAVE_DISALLOWED = "Command-Responses.Group.Leave.Disallowed";
    public static final String COMMAND_GROUP_LEAVE_SUCCESS = "Command-Responses.Group.Leave.Success";

    public static final String COMMAND_GROUP_LIST_NO_GROUPS = "Command-Responses.Group.List.No-Groups";

    public static final String COMMAND_GROUP_MANAGE_NO_SUBCOMMAND = "Command-Responses.Group.Manage.No-Subcommand";

    public static final String COMMAND_GROUP_MANAGE_INVITE_SPECIFY_GROUP = "Command-Responses.Group.Manage.Invite.Specify-Group";
    public static final String COMMAND_GROUP_MANAGE_INVITE_DIFFERENT_UNIVERSES = "Command-Responses.Group.Manage.Invite.Different-Universes";
    public static final String COMMAND_GROUP_MANAGE_INVITE_BAD_LEVEL = "Command-Responses.Group.Manage.Invite.Bad-Level";
    public static final String COMMAND_GROUP_MANAGE_INVITE_DISALLOWED = "Command-Responses.Group.Manage.Invite.Disallowed";
    public static final String COMMAND_GROUP_MANAGE_INVITE_SUCCESS = "Command-Responses.Group.Manage.Invite.Success";

    public static final String COMMAND_GROUP_MANAGE_JOIN_SPECIFY_GROUP = "Command-Responses.Group.Manage.Join.Specify-Group";
    public static final String COMMAND_GROUP_MANAGE_JOIN_ALREADY_HAS_PARENT = "Command-Responses.Group.Manage.Join.Already-Has-Parent";
    public static final String COMMAND_GROUP_MANAGE_JOIN_NOT_INVITED = "Command-Responses.Group.Manage.Join.Not-Invited";
    public static final String COMMAND_GROUP_MANAGE_JOIN_DISALLOWED = "Command-Responses.Group.Manage.Join.Disallowed";
    public static final String COMMAND_GROUP_MANAGE_JOIN_SUCCESS = "Command-Responses.Group.Manage.Join.Success";

    public static final String COMMAND_GROUP_MOTD_NO_MESSAGE = "Command-Responses.Group.MOTD.No-Message";
    public static final String COMMAND_GROUP_MOTD_NOT_MEMBER = "Command-Responses.Group.MOTD.Not-Member";
    public static final String COMMAND_GROUP_MOTD_SET_SUCCESS = "Command-Responses.Group.MOTD.Set-Success";

    public static final String COMMAND_GROUP_ONLINE_HEADING = "Command-Responses.Group.Online.Heading";

    public static final String COMMAND_GROUP_PROMOTE_HIGHER_RANK = "Command-Responses.Group.Promote.Higher-Rank";
    public static final String COMMAND_GROUP_PROMOTE_SPECIFY_PLAYER = "Command-Responses.Group.Promote.Specify-Player";
    public static final String COMMAND_GROUP_PROMOTE_NO_ROLE = "Command-Responses.Group.Promote.No-Role";
    public static final String COMMAND_GROUP_PROMOTE_DISALLOWED = "Command-Responses.Group.Promote.Disallowed";
    public static final String COMMAND_GROUP_PROMOTE_SUCCESS = "Command-Responses.Group.Promote.Success";

    public static final String COMMAND_GROUP_SETROLE_SPECIFY_ARGS = "Command-Responses.Group.Setrole.Specify-Args";
    public static final String COMMAND_GROUP_SETROLE_ROLE_TOO_HIGH = "Command-Responses.Group.Setrole.Role-Too-High";
    public static final String COMMAND_GROUP_SETROLE_DISALLOWED = "Command-Responses.Group.Setrole.Disallowed";
    public static final String COMMAND_GROUP_SETROLE_SUCCESS = "Command-Responses.Group.Setrole.Success";

    public static final String COMMAND_GROUP_SETSPAWN_SPECIFY_PLAYER = "Command-Responses.Group.Setspawn.Specify-Player";
    public static final String COMMAND_GROUP_SETSPAWN_PLOT_NOT_OWNED = "Command-Responses.Group.Setspawn.Plot-Not-Owned";
    public static final String COMMAND_GROUP_SETSPAWN_SUCCESS = "Command-Responses.Group.Setspawn.Success";
    // end

    // todo add keys

    private MessageKeys() {
        throw new UnsupportedOperationException();
    }
}
