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
package net.oliverstanley.politics.util.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serializes and deserializes certain types which Politics stores.
 */
public class PropertySerializer {
    /**
     * Converts a {@link Location} to a storable {@link String}.
     *
     * @param loc the Location to serialize
     * @return serialized representation of the given object
     */
    public static String serializeLocation(Location loc) {
        return "t/" + loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getPitch() + "," + loc.getYaw();
    }

    /**
     * Deserializes the given serial {@link String} into a {@link Location}.
     *
     * @param serialized the serial String to read data from
     * @return deserialized Location based on the given serial String
     * @throws PropertyDeserializationException if the serialized string is invalid
     */
    public static Location deserializeLocation(String serialized) throws PropertyDeserializationException {
        String[] parts1 = serialized.split("/");
        if (parts1.length != 2) {
            throw new PropertyDeserializationException("Not a serialized property!");
        }
        if (!parts1[0].equalsIgnoreCase("t")) {
            throw new PropertyDeserializationException("Not a transform!");
        }

        String[] whatMatters = parts1[1].split(",");
        if (whatMatters.length < 6) {
            throw new PropertyDeserializationException("Not enough transform data!");
        }

        World world = Bukkit.getWorld(whatMatters[0]);
        if (world == null) {
            throw new PropertyDeserializationException("The world '" + whatMatters[0] + "' no longer exists!");
        }

        int x;
        int y;
        int z;
        float qx;
        float qy;
        try {
            x = Integer.parseInt(whatMatters[1]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The x is not an int!", ex);
        }
        try {
            y = Integer.parseInt(whatMatters[2]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The y is not an int!", ex);
        }
        try {
            z = Integer.parseInt(whatMatters[3]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The z is not an int!", ex);
        }
        try {
            qx = Float.parseFloat(whatMatters[4]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The qx is not a float!", ex);
        }
        try {
            qy = Float.parseFloat(whatMatters[5]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The qy is not a float!", ex);
        }

        return new Location(world, x, y, z, qx, qy);
    }

    private static final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Transforms the given {@link LocalDateTime} object to a {@link String}, using as a formatter
     * {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
     *
     * @param object the LocalDateTime object to serialize
     * @return the serial String representing the given LocalDateTime
     */
    public static String serializeLocalDateTime(LocalDateTime object) {
        return object.format(localDateTimeFormatter);
    }

    /**
     * Deserializes a {@link LocalDateTime} object from the given serial {@link String}. The given String must be of the
     * format produced by {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
     *
     * @param serial the serial String to deserialize
     * @return the deserialized LocalDateTime object
     */
    public static LocalDateTime deserializeLocalDateTime(String serial) {
        return LocalDateTime.from(localDateTimeFormatter.parse(serial));
    }

    private PropertySerializer() {
    }
}
