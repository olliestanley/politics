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
package pw.ollie.politics.util;

import pw.ollie.politics.AbstractPoliticsTest;
import pw.ollie.politics.world.PoliticsWorld;
import pw.ollie.politics.world.WorldConfig;

import org.bson.BasicBSONObject;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

public final class PoliticsTestReflection {
    private static final Lookup lookup = MethodHandles.lookup();

    public static WorldConfig instantiateWorldConfig(String name, boolean plots, boolean subplots, Map<String, String> strSettings, Map<String, List<String>> listSettings) {
        try {
            Constructor wcConstructor = WorldConfig.class.getDeclaredConstructor(String.class, boolean.class, boolean.class, Map.class, Map.class);
            wcConstructor.setAccessible(true);
            MethodHandle newWcHandle = lookup.unreflectConstructor(wcConstructor);
            return (WorldConfig) newWcHandle.invoke(name, plots, subplots, strSettings, listSettings);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static PoliticsWorld instantiateDefaultWorld(BasicBSONObject bson) {
        try {
            String name = AbstractPoliticsTest.TEST_WORLD_NAME;
            WorldConfig defaultConfig = PoliticsTestObjectFactory.newDefaultWorldConfig();
            Constructor pwConstructor = PoliticsWorld.class.getDeclaredConstructor(String.class, WorldConfig.class, BasicBSONObject.class);
            pwConstructor.setAccessible(true);
            MethodHandle newPwHandle = lookup.unreflectConstructor(pwConstructor);
            return (PoliticsWorld) newPwHandle.invoke(name, defaultConfig, bson);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private PoliticsTestReflection() {
        throw new UnsupportedOperationException();
    }
}
