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
import be.seeseemelk.mockbukkit.WorldMock;

import pw.ollie.politics.mock.PoliticsMockObjectFactory;
import pw.ollie.politics.universe.UniverseRules;
import pw.ollie.politics.util.PoliticsEventCounter;
import pw.ollie.politics.world.PoliticsWorld;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.Collections;

public abstract class AbstractPoliticsTest {
    protected Server server;
    protected PoliticsPlugin plugin;

    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(PoliticsPlugin.class);
    }

    public abstract void runTest();

    public void tearDown() {
        MockBukkit.unload();
    }

    // creates a universe named Default, in a MockWorld called World, with the default testing UniverseRules
    protected void createDefaultUniverse() {
        // creates mock world with name 'World'
        World world = new WorldMock(Material.DIRT, 3);
        PoliticsWorld politicsWorld = plugin.getWorldManager().getWorld(world);
        UniverseRules defaultRules = PoliticsMockObjectFactory.mockDefaultUniverseRules();
        plugin.getUniverseManager().createUniverse("Default", defaultRules, Collections.singletonList(politicsWorld));
    }

    protected PoliticsEventCounter registerEventCounter() {
        PoliticsEventCounter counter = new PoliticsEventCounter();
        server.getPluginManager().registerEvents(counter, plugin);
        return counter;
    }
}
