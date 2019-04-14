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
import pw.ollie.politics.command.PoliticsBaseCommand;
import pw.ollie.politics.group.level.GroupLevel;

public final class GroupCommand extends PoliticsBaseCommand {
    public GroupCommand(PoliticsPlugin plugin, GroupLevel groupLevel) {
        super(plugin, groupLevel.getName(), "Base command for " + groupLevel.getName() + "-related actions.");

        this.registerSubCommand(new GroupAddCommand(groupLevel));
        this.registerSubCommand(new GroupClaimCommand(groupLevel));
        this.registerSubCommand(new GroupCreateCommand(groupLevel));
        this.registerSubCommand(new GroupDemoteCommand(groupLevel));
        this.registerSubCommand(new GroupDestroyCommand(groupLevel));
        this.registerSubCommand(new GroupInfoCommand(groupLevel));
        this.registerSubCommand(new GroupInviteCommand(groupLevel));
        this.registerSubCommand(new GroupJoinCommand(groupLevel));
        this.registerSubCommand(new GroupKickCommand(groupLevel));
        this.registerSubCommand(new GroupLeaveCommand(groupLevel));
        this.registerSubCommand(new GroupListCommand(groupLevel));
        this.registerSubCommand(new GroupManageCommand(groupLevel));
        this.registerSubCommand(new GroupOnlineCommand(groupLevel));
        this.registerSubCommand(new GroupPromoteCommand(groupLevel));
        this.registerSubCommand(new GroupSetRoleCommand(groupLevel));
        this.registerSubCommand(new GroupSetSpawnCommand(groupLevel));
        this.registerSubCommand(new GroupSpawnCommand(groupLevel));
        this.registerSubCommand(new GroupToggleCommand(groupLevel));
        this.registerSubCommand(new GroupUnclaimCommand(groupLevel));
        this.registerSubCommand(new GroupUninviteCommand(groupLevel));
    }
}
