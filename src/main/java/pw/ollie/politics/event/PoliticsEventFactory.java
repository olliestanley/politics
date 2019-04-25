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
package pw.ollie.politics.event;

import pw.ollie.politics.Politics;
import pw.ollie.politics.activity.PoliticsActivity;
import pw.ollie.politics.economy.PoliticsTransferReason;
import pw.ollie.politics.event.activity.ActivityBeginEvent;
import pw.ollie.politics.event.activity.ActivityEndEvent;
import pw.ollie.politics.event.group.GroupBalanceChangeEvent;
import pw.ollie.politics.event.group.GroupChildAddEvent;
import pw.ollie.politics.event.group.GroupChildInviteEvent;
import pw.ollie.politics.event.group.GroupChildRemoveEvent;
import pw.ollie.politics.event.group.GroupClaimPlotEvent;
import pw.ollie.politics.event.group.GroupCreateEvent;
import pw.ollie.politics.event.group.GroupMemberJoinEvent;
import pw.ollie.politics.event.group.GroupMemberLeaveEvent;
import pw.ollie.politics.event.group.GroupMemberRoleChangeEvent;
import pw.ollie.politics.event.group.GroupMemberSpawnEvent;
import pw.ollie.politics.event.group.GroupPropertySetEvent;
import pw.ollie.politics.event.group.GroupTaxImposeEvent;
import pw.ollie.politics.event.group.GroupUnclaimPlotEvent;
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
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.Role;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.group.war.War;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.PlotDamageSource;
import pw.ollie.politics.world.plot.PlotProtectionType;
import pw.ollie.politics.world.plot.Subplot;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.UUID;

/**
 * Static methods for calling events which return the called event for ease of access.
 */
public final class PoliticsEventFactory {
    public static ActivityBeginEvent callActivityBeginEvent(PoliticsActivity activity) {
        return callEvent(new ActivityBeginEvent(activity));
    }

    public static ActivityEndEvent callActivityEndEvent(PoliticsActivity activity) {
        return callEvent(new ActivityEndEvent(activity));
    }

    public static GroupBalanceChangeEvent callGroupBalanceChangeEvent(Group group, double balance, PoliticsTransferReason reason) {
        return callEvent(new GroupBalanceChangeEvent(group, balance, reason));
    }

    public static GroupChildAddEvent callGroupChildAddEvent(Group group, Group child) {
        return callEvent(new GroupChildAddEvent(group, child));
    }

    public static GroupChildInviteEvent callGroupChildInviteEvent(Group group, Group child, CommandSender source) {
        return callEvent(new GroupChildInviteEvent(group, child, source));
    }

    public static GroupChildRemoveEvent callGroupChildRemoveEvent(Group group, Group child, CommandSender source) {
        return callEvent(new GroupChildRemoveEvent(group, child, source));
    }

    public static GroupCreateEvent callGroupCreateEvent(Group group, CommandSender creator) {
        return callEvent(new GroupCreateEvent(group, creator));
    }

    public static GroupClaimPlotEvent callGroupClaimPlotEvent(Group group, Plot plot, CommandSender claimer) {
        return callEvent(new GroupClaimPlotEvent(group, plot, claimer));
    }

    public static GroupMemberJoinEvent callGroupMemberJoinEvent(Group group, OfflinePlayer member, Role role) {
        return callEvent(new GroupMemberJoinEvent(group, member, role));
    }

    public static GroupMemberLeaveEvent callGroupMemberLeaveEvent(Group group, OfflinePlayer member, boolean kick) {
        return callEvent(new GroupMemberLeaveEvent(group, member, kick));
    }

    public static GroupMemberRoleChangeEvent callGroupMemberRoleChangeEvent(Group group, OfflinePlayer member, Role oldRole, Role newRole) {
        return callEvent(new GroupMemberRoleChangeEvent(group, member, oldRole, newRole));
    }

    public static GroupMemberSpawnEvent callGroupMemberSpawnEvent(Group group, OfflinePlayer player) {
        return callEvent(new GroupMemberSpawnEvent(group, player));
    }

    public static GroupPropertySetEvent callGroupPropertySetEvent(Group group, int property, Object value) {
        return callEvent(new GroupPropertySetEvent(group, property, value));
    }

    public static GroupTaxImposeEvent callGroupTaxImposeEvent(Group group, UUID taxedMember, double amount) {
        return callEvent(new GroupTaxImposeEvent(group, taxedMember, amount));
    }

    public static GroupUnclaimPlotEvent callGroupUnclaimPlotEvent(Group group, Plot plot, CommandSender unclaimer) {
        return callEvent(new GroupUnclaimPlotEvent(group, plot, unclaimer));
    }

    public static PlayerPlotChangeEvent callPlayerPlotChangeEvent(Player player, Plot from, Plot to) {
        return callEvent(new PlayerPlotChangeEvent(player, from, to));
    }

    public static PlotOwnerChangeEvent callPlotOwnerChangeEvent(Plot plot, int groupId, boolean add) {
        return callEvent(new PlotOwnerChangeEvent(plot, groupId, add));
    }

    public static PlotProtectionTriggerEvent callPlotProtectionTriggerEvent(Plot plot, Block damaged, PlotDamageSource source, PlotProtectionType type) {
        return callEvent(new PlotProtectionTriggerEvent(plot, damaged, source, type));
    }

    public static SubplotCreateEvent callSubplotCreateEvent(Plot plot, Subplot subplot) {
        return callEvent(new SubplotCreateEvent(plot, subplot));
    }

    public static SubplotDestroyEvent callSubplotDestroyEvent(Plot plot, Subplot subplot) {
        return callEvent(new SubplotDestroyEvent(plot, subplot));
    }

    public static SubplotOwnerChangeEvent callSubplotOwnerChangeEvent(Plot plot, Subplot subplot, UUID oldOwner, UUID newOwner) {
        return callEvent(new SubplotOwnerChangeEvent(plot, subplot, oldOwner, newOwner));
    }

    public static SubplotPrivilegeChangeEvent callSubplotPrivilegeChangeEvent(Plot plot, Subplot subplot, UUID subject, Privilege privilege, boolean granted) {
        return callEvent(new SubplotPrivilegeChangeEvent(plot, subplot, subject, privilege, granted));
    }

    public static SubplotProtectionTriggerEvent callSubplotProtectionTriggerEvent(Plot plot, Subplot subplot, Block damaged, PlotDamageSource source, PlotProtectionType type) {
        return callEvent(new SubplotProtectionTriggerEvent(plot, subplot, damaged, source, type));
    }

    public static UniverseCreateEvent callUniverseCreateEvent(Universe universe) {
        return callEvent(new UniverseCreateEvent(universe));
    }

    public static UniverseDestroyEvent callUniverseDestroyEvent(Universe universe) {
        return callEvent(new UniverseDestroyEvent(universe));
    }

    public static WarBeginEvent callWarBeginEvent(War war) {
        return callEvent(new WarBeginEvent(war));
    }

    public static WarFinishEvent callWarFinishEvent(War war) {
        return callEvent(new WarFinishEvent(war));
    }

    public static <T extends Event> T callEvent(T event) {
        Politics.getServer().getPluginManager().callEvent(event);
        return event;
    }

    private PoliticsEventFactory() {
        throw new UnsupportedOperationException();
    }
}
