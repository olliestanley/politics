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

/**
 * General utilities relating to {@link String}s.
 */
public final class StringUtil {
    public static boolean notEmpty(String string) {
        return !string.isEmpty();
    }

    /**
     * Find Levenshtein distance between two Strings.
     * <p>
     * From https://github.com/thorikawa/levenshtein-distance-search-sample/
     *
     * @param s the first String, not  {@code null}
     * @param t the second String, not  {@code null}
     * @return the result of calculating the levenshtein distance between them
     * @throws IllegalArgumentException if either param is {@code null}
     */
    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        int n = s.length(), m = t.length();

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        int[] p = new int[n + 1], d = new int[n + 1], _d;
        int i, j, cost;
        char tj;

        for (i = 0; i <= n; ++i) {
            p[i] = i;
        }

        for (j = 1; j <= m; ++j) {
            tj = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; ++i) {
                cost = s.charAt(i - 1) == tj ? 0 : 1;
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            _d = p;
            p = d;
            d = _d;
        }

        return p[n];
    }

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
