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

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.universe.Universe;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Manages sending notifications in Politics.
 * <p>
 * Notifications are messages sent to one or more players triggered by certain events. For instance, players may be
 * notified of which groups they are invited to on-login or when they receive an invitation.
 */
public final class Notifier {
    // todo migrate more messages here
    private final PoliticsPlugin plugin;

    public Notifier(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    public void notifyPlayerGroupInvitation(Player player, Group inviter) {
        // todo
    }

    public void notifyPlayerGroupKick(Player player, Group kicker) {
        // todo
    }

    public void notifyManagersGroupAffilitationInvite(Group invited, Group inviter) {
        // todo
    }

    public void notifyMembersGroupPlayerLeave(Group group, Player leaver) {
        // todo
    }

    public void notifyMembersGroupAffiliate(Group child, Group parent) {
        // todo
    }

    public void notifyMembersGroupDisaffiliate(Group child, Group parent) {
        // todo
    }

    public void notifyPlayerGroupMotd(Player player, Group group) {
        // todo
    }

    public void notifyPlayerTerritoryEntry(Player player, Group group) {
        // todo
    }

    public void notifyPlayerTerritoryExit(Player player, Group group) {
        // todo
    }

    public void notifyPlayerWilderness(Player player, Universe universe) {
        MessageUtil.message(player, universe.getRules().getWildernessMessage());
    }

    private void send(FormattedMessage message, Collection<? extends CommandSender> targets) {
        targets.forEach(message::send);
    }
}
