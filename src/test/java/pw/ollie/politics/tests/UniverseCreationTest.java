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
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.PoliticsEventCounter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class UniverseCreationTest extends AbstractPoliticsTest {
    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    @Test
    public void runTest() {
        PoliticsEventCounter eventCounter = this.registerEventCounter();
        this.createDefaultUniverse();

        Universe universe = universeManager.getUniverse("Default");
        Assert.assertNotNull(universe);
        Assert.assertEquals(1, eventCounter.getUniverseCreates());

        GroupLevel householdLevel = groupManager.getGroupLevel("household");
        Assert.assertNotNull(householdLevel);
        GroupLevel townLevel = groupManager.getGroupLevel("town");
        Assert.assertNotNull(townLevel);
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
    }
}
