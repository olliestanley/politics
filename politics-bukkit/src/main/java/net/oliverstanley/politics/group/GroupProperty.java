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
package net.oliverstanley.politics.group;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Map;

/**
 * Contains static constant id numbers for various different {@link Group} properties, as well as helper methods
 * relating to Group properties..
 */
public final class GroupProperty {
    /**
     * The property id for Group tags.
     */
    public static final int TAG = 0x0;
    /**
     * The property id for Group names.
     */
    public static final int NAME = 0x1;
    /**
     * The property id for Group spawn locations.
     */
    public static final int SPAWN = 0x2;
    /**
     * The property id for Group open status.
     */
    public static final int OPEN = 0x3;
    /**
     * The property id for Group messages of the day.
     */
    public static final int MOTD = 0x4;
    /**
     * The property id for Group descriptions.
     */
    public static final int DESCRIPTION = 0x5;
    /**
     * The property id for Group monetary balances.
     */
    public static final int BALANCE = 0x6;
    /**
     * The property id for Group war victory counts.
     */
    public static final int WAR_VICTORIES = 0x7;
    /**
     * The property id for Group war defeat counts.
     */
    public static final int WAR_DEFEATS = 0x8;
    /**
     * The property id for Group peaceful status.
     */
    public static final int PEACEFUL = 0x9;
    /**
     * The property id for Group territorial entry messages.
     */
    public static final int ENTRY_MESSAGE = 0x10;
    /**
     * The property id for Group territorial exit messages.
     */
    public static final int EXIT_MESSAGE = 0x11;
    /**
     * The property id for Group fixed tax amounts.
     */
    public static final int FIXED_TAX = 0x12;

    private static final TIntSet keyProperties;
    private static final Map<String, Integer> toggleableProperties = new THashMap<>();

    static {
        keyProperties = new TIntHashSet(2);
        keyProperties.add(NAME);
        keyProperties.add(TAG);

        toggleableProperties.put("open", GroupProperty.OPEN);
        toggleableProperties.put("peaceful", GroupProperty.PEACEFUL);
    }

    public static boolean isKeyProperty(int propertyId) {
        return keyProperties.contains(propertyId);
    }

    /**
     * Checks whether there is a toggleable property with the given name.
     *
     * @param name the toggleable property name to test for
     * @return whether a toggleable property with the given name exists
     */
    public static boolean isToggleable(String name) {
        return toggleableProperties.containsKey(name);
    }

    /**
     * Gets the id of toggleable property from its name, if one exists.
     *
     * @param name the name of the toggleable property
     * @return the property id for the toggleable property with the given name
     */
    public static int getToggleablePropertyId(String name) {
        return toggleableProperties.get(name);
    }

    private GroupProperty() {
        throw new UnsupportedOperationException();
    }
}
