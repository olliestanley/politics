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
import pw.ollie.politics.group.war.War;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.PoliticsTestReflection;
import pw.ollie.politics.util.math.Vector3i;
import pw.ollie.politics.util.math.geo.Cuboid;
import pw.ollie.politics.world.PoliticsWorld;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.Subplot;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public final class StorablesTest extends AbstractPoliticsTest {
    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    @Test
    public void runTest() {
        // test universe storage
        Universe universe = createDefaultUniverse();
        BSONObject universeBson = universe.toBSONObject();
        Universe restoredUniverse = Universe.fromBSONObject(universeBson);
        Assert.assertNotNull(restoredUniverse);
        Assert.assertEquals(universe.getName(), restoredUniverse.getName());
        Assert.assertEquals(universe.getGroups().size(), restoredUniverse.getGroups().size());

        // test group storage
        Group group = createTestHousehold();
        BSONObject groupBson = group.toBSONObject();
        Group restoredGroup = Group.fromBSONObject(universe.getRules(), groupBson);
        Assert.assertNotNull(restoredGroup);
        Assert.assertEquals(group.getName(), restoredGroup.getName());
        Assert.assertEquals(group.getTag(), restoredGroup.getTag());

        // test war storage
        Group aggressor = createTestTown("Aggressor");
        Group defender = createTestTown("Defender");
        War war = new War(aggressor, defender);
        PoliticsTestReflection.setWarActive(war, true);
        LocalDateTime startTime = war.getStartTime();
        BasicBSONObject warBson = (BasicBSONObject) war.toBSONObject();
        War restoredWar = PoliticsTestReflection.instantiateWar(warBson);
        Assert.assertEquals(startTime, restoredWar.getStartTime());
        Assert.assertTrue(restoredWar.isActive());

        // test politics world storage
        int testPlotX = 1, testPlotY = 1;
        World world = server.getWorld(TEST_WORLD_NAME);
        Group ownerHousehold = createTestHousehold("PlotLovers");
        int ownerGroupId = ownerHousehold.getUid();
        Player ownerMember = server.getPlayer(0);
        UUID memberId = ownerMember.getUniqueId();
        ownerHousehold.setRole(memberId, ownerHousehold.getLevel().getFounder());

        PoliticsWorld politicsWorld = plugin.getWorldManager().getWorld(world);
        Plot plot = politicsWorld.getPlotAtChunkPosition(testPlotX, testPlotY);
        plot.setOwner(ownerHousehold);
        plot.createSubplot(new Cuboid(plot.getBasePoint(), new Vector3i(1, 1, 1)), memberId);
        BasicBSONObject worldBson = (BasicBSONObject) politicsWorld.toBSONObject();
        PoliticsWorld restoredWorld = PoliticsTestReflection.instantiateDefaultWorld(worldBson);
        Assert.assertEquals(TEST_WORLD_NAME, restoredWorld.getName());
        Plot restoredPlot = restoredWorld.getPlotAtChunkPosition(testPlotX, testPlotY);
        Assert.assertEquals(ownerGroupId, restoredPlot.getOwnerId());
        Assert.assertEquals(1, restoredPlot.getSubplotQuantity());
        Subplot subplot = restoredPlot.getSubplot(0);
        Assert.assertNotNull(subplot);
        Assert.assertEquals(memberId, subplot.getOwnerId());
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
    }
}
