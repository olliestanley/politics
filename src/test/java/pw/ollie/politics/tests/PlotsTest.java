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
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.group.privilege.PrivilegeType;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.PoliticsEventCounter;
import pw.ollie.politics.world.plot.Plot;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class PlotsTest extends AbstractPoliticsTest {
    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    @Test
    public void runTest() {
        // setup
        PoliticsEventCounter eventCounter = this.registerEventCounter();
        this.createDefaultUniverse();
        Player founder = server.getPlayer(0);
        Universe universe = universeManager.getUniverse("Default");
        GroupLevel householdLevel = groupManager.getGroupLevel("household");
        Group household = universe.createGroup(householdLevel);
        household.setRole(founder.getUniqueId(), householdLevel.getFounder());
        World world = server.getWorld("World");

        // plot creation testing
        Chunk chunk11 = world.getChunkAt(1, 1);
        Plot plot = worldManager.getPlotAtChunk(chunk11);
        Assert.assertNull(plot.getOwner());
        plot.setOwner(household);
        Assert.assertEquals(1, eventCounter.getPlotOwnerChanges());
        Assert.assertTrue(plot.isOwner(household));
        Assert.assertTrue(plot.isIndirectOwner(household));

        // plot privilege testing
        Player groupless = server.getPlayer(2);
        for (Privilege privilege : Privileges.all()) {
            if (privilege.getTypes().contains(PrivilegeType.PLOT)) {
                Assert.assertTrue(plot.can(founder, privilege));
                Assert.assertFalse(plot.can(groupless, privilege));
            }
        }
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
    }
}
