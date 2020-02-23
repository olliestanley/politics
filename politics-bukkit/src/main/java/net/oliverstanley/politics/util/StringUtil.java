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
package net.oliverstanley.politics.util;

/**
 * General utilities relating to {@link String}s.
 */
public final class StringUtil {
    /**
     * Capitalises the first character of the given {@link String}, using {@link Character#toTitleCase(char)}.
     *
     * @param string the String to capitalise the first character of
     * @return the given String but with the first character capitalised
     */
    public static String capitaliseFirst(String string) {
        if (string == null) {
            return null;
        }
        if (string.isEmpty()) {
            return string;
        }
        char[] array = string.toCharArray();
        char first = Character.toTitleCase(array[0]);
        array[0] = first;
        return String.valueOf(array);
    }

    private StringUtil() {
        throw new UnsupportedOperationException();
    }
}
