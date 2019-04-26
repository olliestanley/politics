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
package pw.ollie.politics.util;

import pw.ollie.politics.event.activity.ActivityBeginEvent;
import pw.ollie.politics.event.activity.ActivityEndEvent;
import pw.ollie.politics.event.group.GroupBalanceChangeEvent;
import pw.ollie.politics.event.group.GroupChildAddEvent;
import pw.ollie.politics.event.group.GroupChildInviteEvent;
import pw.ollie.politics.event.group.GroupChildRemoveEvent;
import pw.ollie.politics.event.group.GroupCreateEvent;
import pw.ollie.politics.event.group.GroupMemberJoinEvent;
import pw.ollie.politics.event.group.GroupMemberLeaveEvent;
import pw.ollie.politics.event.group.GroupMemberRoleChangeEvent;
import pw.ollie.politics.event.group.GroupMemberSpawnEvent;
import pw.ollie.politics.event.group.GroupPlotClaimEvent;
import pw.ollie.politics.event.group.GroupPlotUnclaimEvent;
import pw.ollie.politics.event.group.GroupPropertySetEvent;
import pw.ollie.politics.event.group.GroupTaxImposeEvent;
import pw.ollie.politics.event.player.PlayerPlotChangeEvent;
import pw.ollie.politics.event.plot.PlotOwnerChangeEvent;
import pw.ollie.politics.event.plot.PlotProtectionTriggerEvent;
import pw.ollie.politics.event.plot.subplot.SubplotCreateEvent;
import pw.ollie.politics.event.plot.subplot.SubplotDestroyEvent;
import pw.ollie.politics.event.plot.subplot.SubplotOwnerChangeEvent;
import pw.ollie.politics.event.plot.subplot.SubplotPrivilegeChangeEvent;
import pw.ollie.politics.event.plot.subplot.SubplotProtectionTriggerEvent;
import pw.ollie.politics.event.universe.UniverseCreateEvent;
import pw.ollie.politics.event.universe.UniverseDestroyEvent;
import pw.ollie.politics.event.war.WarBeginEvent;
import pw.ollie.politics.event.war.WarFinishEvent;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PoliticsEventCounter implements Listener {
    private int activityBegin = 0;
    private int activityEnd = 0;
    private int groupBalanceChange = 0;
    private int groupChildAdd = 0;
    private int groupChildInvite = 0;
    private int groupChildRemove = 0;
    private int groupCreate = 0;
    private int groupMemberJoin = 0;
    private int groupMemberLeave = 0;
    private int groupMemberRoleChange = 0;
    private int groupMemberSpawn = 0;
    private int groupPlotClaim = 0;
    private int groupPlotUnclaim = 0;
    private int groupPropertySet = 0;
    private int groupTaxImpose = 0;
    private int playerPlotChange = 0;
    private int plotOwnerChange = 0;
    private int plotProtectionTrigger = 0;
    private int subplotCreate = 0;
    private int subplotDestroy = 0;
    private int subplotOwnerChange = 0;
    private int subplotPrivilegeChange = 0;
    private int subplotProtectionTrigger = 0;
    private int universeCreate = 0;
    private int universeDestroy = 0;
    private int warBegin = 0;
    private int warFinish = 0;

    @EventHandler
    public void event(Event event) {
        if (event instanceof ActivityBeginEvent) {
            activityBegin++;
        } else if (event instanceof ActivityEndEvent) {
            activityEnd++;
        } else if (event instanceof GroupBalanceChangeEvent) {
            groupBalanceChange++;
        } else if (event instanceof GroupChildAddEvent) {
            groupChildAdd++;
        } else if (event instanceof GroupChildInviteEvent) {
            groupChildInvite++;
        } else if (event instanceof GroupChildRemoveEvent) {
            groupChildRemove++;
        } else if (event instanceof GroupCreateEvent) {
            groupCreate++;
        } else if (event instanceof GroupMemberJoinEvent) {
            groupMemberJoin++;
        } else if (event instanceof GroupMemberLeaveEvent) {
            groupMemberLeave++;
        } else if (event instanceof GroupMemberRoleChangeEvent) {
            groupMemberRoleChange++;
        } else if (event instanceof GroupMemberSpawnEvent) {
            groupMemberSpawn++;
        } else if (event instanceof GroupPlotClaimEvent) {
            groupPlotClaim++;
        } else if (event instanceof GroupPlotUnclaimEvent) {
            groupPlotUnclaim++;
        } else if (event instanceof GroupPropertySetEvent) {
            groupPropertySet++;
        } else if (event instanceof GroupTaxImposeEvent) {
            groupTaxImpose++;
        } else if (event instanceof PlayerPlotChangeEvent) {
            playerPlotChange++;
        } else if (event instanceof PlotOwnerChangeEvent) {
            plotOwnerChange++;
        } else if (event instanceof PlotProtectionTriggerEvent) {
            plotProtectionTrigger++;
        } else if (event instanceof SubplotCreateEvent) {
            subplotCreate++;
        } else if (event instanceof SubplotDestroyEvent) {
            subplotDestroy++;
        } else if (event instanceof SubplotOwnerChangeEvent) {
            subplotOwnerChange++;
        } else if (event instanceof SubplotPrivilegeChangeEvent) {
            subplotPrivilegeChange++;
        } else if (event instanceof SubplotProtectionTriggerEvent) {
            subplotProtectionTrigger++;
        } else if (event instanceof UniverseCreateEvent) {
            universeCreate++;
        } else if (event instanceof UniverseDestroyEvent) {
            universeDestroy++;
        } else if (event instanceof WarBeginEvent) {
            warBegin++;
        } else if (event instanceof WarFinishEvent) {
            warFinish++;
        }
    }

    public int getActivityBegins() {
        return activityBegin;
    }

    public int getActivityEnds() {
        return activityEnd;
    }

    public int getGroupBalanceChanges() {
        return groupBalanceChange;
    }

    public int getGroupChildAdds() {
        return groupChildAdd;
    }

    public int getGroupChildInvites() {
        return groupChildInvite;
    }

    public int getGroupChildRemoves() {
        return groupChildRemove;
    }

    public int getGroupCreates() {
        return groupCreate;
    }

    public int getGroupMemberJoins() {
        return groupMemberJoin;
    }

    public int getGroupMemberLeaves() {
        return groupMemberLeave;
    }

    public int getGroupMemberRoleChanges() {
        return groupMemberRoleChange;
    }

    public int getGroupMemberSpawns() {
        return groupMemberSpawn;
    }

    public int getGroupPlotClaims() {
        return groupPlotClaim;
    }

    public int getGroupPlotUnclaims() {
        return groupPlotUnclaim;
    }

    public int getGroupPropertySets() {
        return groupPropertySet;
    }

    public int getGroupTaxImposes() {
        return groupTaxImpose;
    }

    public int getPlayerPlotChanges() {
        return playerPlotChange;
    }

    public int getPlotOwnerChanges() {
        return plotOwnerChange;
    }

    public int getPlotProtectionTriggers() {
        return plotProtectionTrigger;
    }

    public int getSubplotCreates() {
        return subplotCreate;
    }

    public int getSubplotDestroys() {
        return subplotDestroy;
    }

    public int getSubplotOwnerChanges() {
        return subplotOwnerChange;
    }

    public int getSubplotPrivilegeChanges() {
        return subplotPrivilegeChange;
    }

    public int getSubplotProtectionTriggers() {
        return subplotProtectionTrigger;
    }

    public int getUniverseCreates() {
        return universeCreate;
    }

    public int getUniverseDestroys() {
        return universeDestroy;
    }

    public int getWarBegins() {
        return warBegin;
    }

    public int getWarFinishes() {
        return warFinish;
    }
}
