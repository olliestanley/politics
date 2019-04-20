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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.group.level.GroupLevel;

import java.util.List;
import java.util.Set;

public final class GroupManager {
    private final PoliticsPlugin plugin;
    // note: affiliation requests are non-persistent
    private final TIntObjectMap<Set<GroupAffiliationRequest>> affiliationInvites;

    public GroupManager(PoliticsPlugin plugin) {
        this.plugin = plugin;
        this.affiliationInvites = new TIntObjectHashMap<>();

        plugin.getServer().getPluginManager().registerEvents(new GroupCombatProtectionListener(plugin), plugin);
    }

    public Set<GroupAffiliationRequest> getAffiliationRequests(int group) {
        Set<GroupAffiliationRequest> requests = affiliationInvites.get(group);
        if (requests != null) {
            return new THashSet<>(requests);
        }
        return new THashSet<>();
    }

    public Set<GroupAffiliationRequest> getAffiliationRequests(Group group) {
        return getAffiliationRequests(group.getUid());
    }

    public boolean addAffiliationRequest(GroupAffiliationRequest request) {
        Group sender = getGroupById(request.getSender());
        Group recipient = getGroupById(request.getRecipient());
        if (sender == null || recipient == null) {
            return false;
        }

        PoliticsEventFactory.callGroupChildInviteEvent(sender, recipient);
        affiliationInvites.putIfAbsent(request.getRecipient(), new THashSet<>());
        affiliationInvites.get(request.getRecipient()).add(request);
        return true;
    }

    public boolean removeAffiliationRequest(GroupAffiliationRequest request) {
        Set<GroupAffiliationRequest> requests = affiliationInvites.get(request.getRecipient());
        if (requests != null) {
            return requests.remove(request);
        }
        return false;
    }

    public List<GroupLevel> getGroupLevels() {
        return plugin.getUniverseManager().getGroupLevels();
    }

    public Group getGroupById(int id) {
        return plugin.getUniverseManager().getGroupById(id);
    }

    public Group getGroupByTag(String tag) {
        return plugin.getUniverseManager().getGroupByTag(tag);
    }

    public PoliticsPlugin getPlugin() {
        return plugin;
    }
}
