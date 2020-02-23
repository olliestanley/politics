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
package net.oliverstanley.politics.listener;

import net.oliverstanley.politics.PoliticsPlugin;
import net.oliverstanley.politics.group.Group;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;

/**
 * Listens to events to apply relevant {@link Group}-related protections to players.
 */
public final class CombatListener implements Listener {
    private final PoliticsPlugin plugin;

    public CombatListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean shouldPrevent(Player attacker, Player attacked) {
        return plugin.getUniverseHandler().citizenGroups(attacked.getUniqueId())
                .filter(plugin.getWorldHandler().getWorld(attacked.getWorld())::hasGroup)
                .filter(this::noFriendlyFire).anyMatch(group -> group.isMember(attacker.getUniqueId()));
    }

    private boolean noFriendlyFire(Group group) {
        return !group.getLevel().isFriendlyFire();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (!(damaged instanceof Player && damager instanceof Player)) {
            return;
        }

        if (shouldPrevent((Player) damager, (Player) damaged)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        // todo prevent damage from potion splash between friendlies
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getCombuster();
        if (!(damaged instanceof Player && damager instanceof Player)) {
            return;
        }

        if (shouldPrevent((Player) damager, (Player) damaged)) {
            event.setCancelled(true);
        }
    }
}
