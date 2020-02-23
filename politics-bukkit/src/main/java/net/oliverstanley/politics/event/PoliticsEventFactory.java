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
package net.oliverstanley.politics.event;

import net.oliverstanley.politics.Politics;
import net.oliverstanley.politics.event.group.GroupChildAddEvent;
import net.oliverstanley.politics.event.group.GroupChildInviteEvent;
import net.oliverstanley.politics.event.group.GroupChildRemoveEvent;
import net.oliverstanley.politics.event.group.GroupCreateEvent;
import net.oliverstanley.politics.event.group.GroupMemberJoinEvent;
import net.oliverstanley.politics.event.group.GroupMemberLeaveEvent;
import net.oliverstanley.politics.event.group.GroupMemberRoleChangeEvent;
import net.oliverstanley.politics.event.group.GroupMemberSpawnEvent;
import net.oliverstanley.politics.event.group.GroupPlotClaimEvent;
import net.oliverstanley.politics.event.group.GroupPlotUnclaimEvent;
import net.oliverstanley.politics.event.group.GroupPropertySetEvent;
import net.oliverstanley.politics.event.group.GroupTaxImposeEvent;
import net.oliverstanley.politics.event.player.PlayerPlotChangeEvent;
import net.oliverstanley.politics.event.plot.PlotOwnerChangeEvent;
import net.oliverstanley.politics.event.plot.PlotProtectionTriggerEvent;
import net.oliverstanley.politics.event.plot.subplot.SubplotCreateEvent;
import net.oliverstanley.politics.event.plot.subplot.SubplotDestroyEvent;
import net.oliverstanley.politics.event.plot.subplot.SubplotOwnerChangeEvent;
import net.oliverstanley.politics.event.plot.subplot.SubplotPrivilegeChangeEvent;
import net.oliverstanley.politics.event.plot.subplot.SubplotProtectionTriggerEvent;
import net.oliverstanley.politics.event.universe.UniverseCreateEvent;
import net.oliverstanley.politics.event.universe.UniverseDestroyEvent;
import net.oliverstanley.politics.group.Group;
import net.oliverstanley.politics.group.Role;
import net.oliverstanley.politics.privilege.Privilege;
import net.oliverstanley.politics.universe.Universe;
import net.oliverstanley.politics.world.plot.Plot;
import net.oliverstanley.politics.world.plot.PlotDamageSource;
import net.oliverstanley.politics.world.plot.PlotProtectionType;
import net.oliverstanley.politics.world.plot.Subplot;

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

    public static GroupMemberJoinEvent callGroupMemberJoinEvent(Group group, OfflinePlayer member, Role role) {
        return callEvent(new GroupMemberJoinEvent(group, member, role));
    }

    public static GroupMemberLeaveEvent callGroupMemberLeaveEvent(Group group, Player member) {
        return callEvent(new GroupMemberLeaveEvent(group, member, null));
    }

    public static GroupMemberLeaveEvent callGroupMemberLeaveEvent(Group group, OfflinePlayer member, CommandSender kicker) {
        return callEvent(new GroupMemberLeaveEvent(group, member, kicker));
    }

    public static GroupMemberRoleChangeEvent callGroupMemberRoleChangeEvent(Group group, OfflinePlayer member, Role oldRole, Role newRole, CommandSender source) {
        return callEvent(new GroupMemberRoleChangeEvent(group, member, oldRole, newRole, source));
    }

    public static GroupMemberSpawnEvent callGroupMemberSpawnEvent(Group group, OfflinePlayer player, CommandSender source) {
        return callEvent(new GroupMemberSpawnEvent(group, player, source));
    }

    public static GroupPropertySetEvent callGroupPropertySetEvent(Group group, int property, Object value) {
        return callEvent(new GroupPropertySetEvent(group, property, value));
    }

    public static GroupTaxImposeEvent callGroupTaxImposeEvent(Group group, UUID taxedMember, double amount) {
        return callEvent(new GroupTaxImposeEvent(group, taxedMember, amount));
    }

    public static GroupPlotClaimEvent callGroupPlotClaimEvent(Group group, Plot plot, CommandSender claimer) {
        return callEvent(new GroupPlotClaimEvent(group, plot, claimer));
    }

    public static GroupPlotUnclaimEvent callGroupPlotUnclaimEvent(Group group, Plot plot, CommandSender unclaimer) {
        return callEvent(new GroupPlotUnclaimEvent(group, plot, unclaimer));
    }

    public static PlayerPlotChangeEvent callPlayerPlotChangeEvent(Player player, Plot from, Plot to) {
        return callEvent(new PlayerPlotChangeEvent(player, from, to));
    }

    public static PlotOwnerChangeEvent callPlotOwnerChangeEvent(Plot plot, int groupId, boolean add) {
        return callEvent(new PlotOwnerChangeEvent(plot, groupId, add));
    }

    public static PlotProtectionTriggerEvent callPlotProtectionTriggerEvent(Plot plot, Block damaged, PlotDamageSource source, PlotProtectionType type, Event cause) {
        return callEvent(new PlotProtectionTriggerEvent(plot, damaged, source, type, cause));
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

    public static SubplotProtectionTriggerEvent callSubplotProtectionTriggerEvent(Plot plot, Subplot subplot, Block damaged, PlotDamageSource source, PlotProtectionType type, Event cause) {
        return callEvent(new SubplotProtectionTriggerEvent(plot, subplot, damaged, source, type, cause));
    }

    public static UniverseCreateEvent callUniverseCreateEvent(Universe universe) {
        return callEvent(new UniverseCreateEvent(universe));
    }

    public static UniverseDestroyEvent callUniverseDestroyEvent(Universe universe) {
        return callEvent(new UniverseDestroyEvent(universe));
    }

    private static <T extends Event> T callEvent(T event) {
        Politics.getServer().getPluginManager().callEvent(event);
        return event;
    }

    private PoliticsEventFactory() {
        throw new UnsupportedOperationException();
    }
}
