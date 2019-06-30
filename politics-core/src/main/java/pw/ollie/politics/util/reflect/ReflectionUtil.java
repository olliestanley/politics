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
package pw.ollie.politics.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public final class ReflectionUtil {
    private static Predicate<Integer> constantTest = mod -> Modifier.isFinal(mod) && Modifier.isStatic(mod) && Modifier.isPublic(mod);

    public static Object getAccessibleFieldValue(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static boolean isConstant(Field field) {
        return constantTest.test(field.getModifiers());
    }

    private ReflectionUtil() {
        throw new UnsupportedOperationException();
    }
}
