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
package pw.ollie.politics.group.protection;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupManager;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.universe.UniverseManager;
import pw.ollie.politics.world.PoliticsWorld;
import pw.ollie.politics.world.WorldManager;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GroupCombatProtectionListener implements Listener {
    private final PoliticsPlugin plugin;

    public GroupCombatProtectionListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damagedEntity = event.getEntity();
        if (!(damagedEntity instanceof Player)) {
            return;
        }
        Entity damagerEntity = event.getDamager();
        if (!(damagerEntity instanceof Player)) {
            return;
        }

        Player damaged = (Player) damagedEntity;

        GroupManager groupManager = plugin.getGroupManager();
        WorldManager worldManager = plugin.getWorldManager();

        List<GroupLevel> groupLevels = groupManager.getGroupLevels();
        World bukkitWorld = damaged.getWorld();
        PoliticsWorld world = worldManager.getWorld(bukkitWorld);

        UniverseManager universeManager = plugin.getUniverseManager();
        UUID damagedId = damaged.getUniqueId();
        Player damager = (Player) damagerEntity;
        UUID damagerId = damager.getUniqueId();

        for (GroupLevel groupLevel : groupLevels) {
            if (groupLevel.isFriendlyFire()) {
                continue;
            }

            Universe universe = universeManager.getUniverse(world, groupLevel);
            if (universe == null) {
                continue;
            }

            Set<Group> damagedGroups = universe.getCitizenGroups(damagedId);
            for (Group damagedGroup : damagedGroups) {
                if (damagedGroup.isImmediateMember(damagerId)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
