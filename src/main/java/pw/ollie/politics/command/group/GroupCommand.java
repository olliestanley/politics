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
    public GroupCommand(PoliticsPlugin plugin, GroupLevel level) {
        super(plugin, level.getName(), "Base command for " + level.getName() + "-related actions.");

        this.registerSubCommand(new GroupAddCommand(level));
        this.registerSubCommand(new GroupClaimCommand(level));
        this.registerSubCommand(new GroupCreateCommand(level));
        this.registerSubCommand(new GroupDemoteCommand(level));
        this.registerSubCommand(new GroupDescriptionCommand(level));
        this.registerSubCommand(new GroupDestroyCommand(level));
        this.registerSubCommand(new GroupEntryCommand(level));
        this.registerSubCommand(new GroupExitCommand(level));
        this.registerSubCommand(new GroupHelpCommand(level));
        this.registerSubCommand(new GroupInfoCommand(level));
        this.registerSubCommand(new GroupInviteCommand(level));
        this.registerSubCommand(new GroupJoinCommand(level));
        this.registerSubCommand(new GroupKickCommand(level));
        this.registerSubCommand(new GroupLeaveCommand(level));
        this.registerSubCommand(new GroupListCommand(level));
        this.registerSubCommand(new GroupManageCommand(level));
        this.registerSubCommand(new GroupMotdCommand(level));
        this.registerSubCommand(new GroupOnlineCommand(level));
        this.registerSubCommand(new GroupPromoteCommand(level));
        this.registerSubCommand(new GroupSetroleCommand(level));
        this.registerSubCommand(new GroupSetspawnCommand(level));
        this.registerSubCommand(new GroupSettaxCommand(level));
        this.registerSubCommand(new GroupSpawnCommand(level));
        this.registerSubCommand(new GroupToggleCommand(level));
        this.registerSubCommand(new GroupUnclaimCommand(level));
        this.registerSubCommand(new GroupUninviteCommand(level));
    }
}
