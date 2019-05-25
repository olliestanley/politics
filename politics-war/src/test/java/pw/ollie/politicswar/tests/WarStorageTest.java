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
package pw.ollie.politicswar.tests;

import pw.ollie.politics.group.Group;
import pw.ollie.politicswar.AbstractPoliticsWarTest;
import pw.ollie.politicswar.util.PoliticsWarTestReflection;
import pw.ollie.politicswar.war.War;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.bson.BasicBSONObject;

import java.time.LocalDateTime;

public final class WarStorageTest extends AbstractPoliticsWarTest {
    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    @Test
    public void runTest() {
        // test war storage
        Group aggressor = createTestTown("Aggressor");
        Group defender = createTestTown("Defender");
        War war = new War(aggressor, defender);
        PoliticsWarTestReflection.setWarActive(war, true);
        LocalDateTime startTime = war.getStartTime();
        BasicBSONObject warBson = (BasicBSONObject) war.toBSONObject();
        War restoredWar = PoliticsWarTestReflection.instantiateWar(warBson);
        Assert.assertEquals(startTime, restoredWar.getStartTime());
        Assert.assertTrue(restoredWar.isActive());
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
    }
}
