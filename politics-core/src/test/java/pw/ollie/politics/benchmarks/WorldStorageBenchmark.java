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
package pw.ollie.politics.benchmarks;

import pw.ollie.politics.AbstractPoliticsBenchmark;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.util.PoliticsTestObjectFactory;
import pw.ollie.politics.world.PoliticsWorld;
import pw.ollie.politics.world.WorldConfig;
import pw.ollie.politics.world.plot.Plot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bukkit.World;
import org.bukkit.entity.Player;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WorldStorageBenchmark extends AbstractPoliticsBenchmark {
    private WorldConfig defConfig;
    private PoliticsWorld polWorld;
    private BSONObject bson;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        defConfig = PoliticsTestObjectFactory.newDefaultWorldConfig();

        createDefaultUniverse();
        World world = server.getWorld(TEST_WORLD_NAME);
        polWorld = worldManager.getWorld(world);

        Random random = ThreadLocalRandom.current();

        Group townOne = createTestTown("One");
        Player playerOne = server.addPlayer();
        townOne.setRole(playerOne.getUniqueId(), townOne.getLevel().getFounder());

        Group townTwo = createTestTown("Two");
        Player playerTwo = server.addPlayer();
        townTwo.setRole(playerTwo.getUniqueId(), townTwo.getLevel().getFounder());

        for (int i = 0; i < 1000; i++) {
            boolean one = i % 2 == 0;

            Plot plot = polWorld.getPlotAtChunkPosition(random.nextInt(1000), random.nextInt(1000));
            plot.setOwner(one ? townOne : townTwo);

            if (random.nextBoolean()) {
                plot.createSubplot(plot.getCuboid(), one ? playerOne.getUniqueId() : playerTwo.getUniqueId());
            }
        }

        bson = polWorld.toBSONObject();
    }

    @Override
    @Test
    public void runTest() {
        polWorld.toBSONObject();
    }

    @Test
    public void runTestTwo() {
        new PoliticsWorld("world", defConfig, (BasicBSONObject) bson);
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();

        polWorld = null;
    }
}
