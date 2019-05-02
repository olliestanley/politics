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
import pw.ollie.politics.util.Position;
import pw.ollie.politics.util.math.RotatedPosition;
import pw.ollie.politics.util.math.Vector2f;
import pw.ollie.politics.util.serial.PropertyDeserializationException;
import pw.ollie.politics.util.serial.PropertySerializer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public final class PropertySerializerTest extends AbstractPoliticsTest {
    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    @Test
    public void runTest() {
        super.createDefaultUniverse();

        // test serialization and deserialization of RotatedPosition objects
        try {
            RotatedPosition rotatedPosition = new RotatedPosition(new Position(TEST_WORLD_NAME, 0, 0, 0), new Vector2f(0, 0));
            String serial = PropertySerializer.serializeRotatedPosition(rotatedPosition);
            RotatedPosition deserial = PropertySerializer.deserializeRotatedPosition(serial);
            Assert.assertEquals(deserial, rotatedPosition);
        } catch (PropertyDeserializationException e) {
            throw new RuntimeException(e);
        }

        // test serialization and deserialization of LocalDateTime objects
        {
            LocalDateTime dateTime = LocalDateTime.now();
            String serial = PropertySerializer.serializeLocalDateTime(dateTime);
            LocalDateTime deserial = PropertySerializer.deserializeLocalDateTime(serial);
            Assert.assertEquals(deserial, dateTime);
        }
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
    }
}
