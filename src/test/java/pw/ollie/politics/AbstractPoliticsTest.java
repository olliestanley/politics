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
package pw.ollie.politics;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupManager;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.mock.PoliticsMockObjectFactory;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.universe.UniverseManager;
import pw.ollie.politics.universe.UniverseRules;
import pw.ollie.politics.util.PoliticsEventCounter;
import pw.ollie.politics.world.PoliticsWorld;
import pw.ollie.politics.world.WorldManager;

import java.util.Collections;

public abstract class AbstractPoliticsTest {
    public static final String TEST_WORLD_NAME = "world";

    protected ServerMock server;
    protected PoliticsPlugin plugin;
    protected UniverseManager universeManager;
    protected GroupManager groupManager;
    protected WorldManager worldManager;

    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(PoliticsPlugin.class);

        // should be more than enough for the testing we'll be doing
        server.setPlayers(20);

        universeManager = plugin.getUniverseManager();
        groupManager = plugin.getGroupManager();
        worldManager = plugin.getWorldManager();
    }

    public abstract void runTest();

    public void tearDown() {
        MockBukkit.unload();
    }

    // creates a universe named Default, in a MockWorld called world, with the default testing UniverseRules
    protected Universe createDefaultUniverse() {
        Universe existing = universeManager.getUniverse("Default");
        if (existing != null) {
            return existing;
        }
        // creates mock world with name 'World'
        WorldMock world = server.addSimpleWorld(TEST_WORLD_NAME);
        PoliticsWorld politicsWorld = worldManager.getWorld(world);
        UniverseRules defaultRules = PoliticsMockObjectFactory.mockDefaultUniverseRules();
        return universeManager.createUniverse("Default", defaultRules, Collections.singletonList(politicsWorld));
    }

    protected Group createTestHousehold() {
        return createTestHousehold("Test Household");
    }

    protected Group createTestHousehold(String hName) {
        Universe universe = createDefaultUniverse();
        GroupLevel householdLevel = groupManager.getGroupLevel("household");
        Group household = universe.createGroup(householdLevel);
        String hTag = hName.toLowerCase().replace(" ", "-");
        household.setProperty(GroupProperty.NAME, hName);
        household.setProperty(GroupProperty.TAG, hTag);
        return household;
    }

    protected Group createTestTown() {
        return createTestTown("Test Town");
    }

    protected Group createTestTown(String tName) {
        Universe universe = createDefaultUniverse();
        GroupLevel townLevel = groupManager.getGroupLevel("town");
        Group town = universe.createGroup(townLevel);
        String tTag = tName.toLowerCase().replace(" ", "-");
        town.setProperty(GroupProperty.NAME, tName);
        town.setProperty(GroupProperty.TAG, tTag);
        return town;
    }

    protected PoliticsEventCounter registerEventCounter() {
        PoliticsEventCounter counter = new PoliticsEventCounter();
        server.getPluginManager().registerEvents(counter, plugin);
        return counter;
    }
}
