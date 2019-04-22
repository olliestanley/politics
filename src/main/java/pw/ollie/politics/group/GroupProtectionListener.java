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

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.universe.UniverseManager;
import pw.ollie.politics.world.PoliticsWorld;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class GroupProtectionListener implements Listener {
    private final PoliticsPlugin plugin;

    public GroupProtectionListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (!(damaged instanceof Player && damager instanceof Player)) {
            return;
        }

        PoliticsWorld world = plugin.getWorldManager().getWorld(damaged.getWorld());
        UniverseManager universeManager = plugin.getUniverseManager();
        UUID damagedId = damaged.getUniqueId();
        UUID damagerId = damager.getUniqueId();

        for (GroupLevel groupLevel : plugin.getGroupManager().getGroupLevels()) {
            if (groupLevel.isFriendlyFire()) {
                continue;
            }

            Universe universe = universeManager.getUniverse(world, groupLevel);
            if (universe == null) {
                continue;
            }

            if (universe.getCitizenGroups(damagedId).stream()
                    .anyMatch(group -> group.isImmediateMember(damagerId))) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
