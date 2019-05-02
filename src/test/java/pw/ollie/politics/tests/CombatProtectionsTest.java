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
package pw.ollie.politics.tests;

import pw.ollie.politics.AbstractPoliticsTest;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public final class CombatProtectionsTest extends AbstractPoliticsTest {
    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    @Test
    public void runTest() {
        Player player = server.getPlayer(0);
        Player player2 = server.getPlayer(1);

        // make sure we don't accidentally cancel random events
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(player, player2, DamageCause.ENTITY_ATTACK, 1.0);
        Bukkit.getPluginManager().callEvent(event);
        Assert.assertFalse(event.isCancelled());

        // test that we do cancel friendly fire
        Group household = createTestHousehold();
        GroupLevel hhLevel = household.getLevel();
        household.setRole(player.getUniqueId(), hhLevel.getFounder());
        household.setRole(player2.getUniqueId(), hhLevel.getInitial());
        event = new EntityDamageByEntityEvent(player, player2, DamageCause.ENTITY_ATTACK, 1.0);
        Bukkit.getPluginManager().callEvent(event);
        Assert.assertTrue(event.isCancelled());

        // todo add more test cases, including for wars when implemented
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
    }
}
