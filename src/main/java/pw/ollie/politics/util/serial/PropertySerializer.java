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
package pw.ollie.politics.util.serial;

import pw.ollie.politics.util.Position;
import pw.ollie.politics.util.math.RotatedPosition;
import pw.ollie.politics.util.math.Vector2f;

import org.bukkit.Bukkit;

public class PropertySerializer {
    public static String serializePosition(Position position) {
        return "p/" + position.getWorld() + "," + position.getX() + "," + position.getY() + "," + position.getZ();
    }

    public static Position deserializePosition(String serialized) throws PropertyDeserializationException {
        String[] parts1 = serialized.split("/");
        if (parts1.length != 2) {
            throw new PropertyDeserializationException("Not a serialized property!");
        }
        if (!parts1[0].equalsIgnoreCase("p")) {
            throw new PropertyDeserializationException("Not a position!");
        }

        String[] whatMatters = parts1[1].split(",");
        if (whatMatters.length < 4) {
            throw new PropertyDeserializationException("Not enough position data!");
        }

        String world = whatMatters[0];
        if (Bukkit.getWorld(world) == null) {
            throw new PropertyDeserializationException("The world '" + world + "' no longer exists!");
        }

        float x;
        float y;
        float z;
        try {
            x = Float.parseFloat(whatMatters[1]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The x is not a float!", ex);
        }
        try {
            y = Float.parseFloat(whatMatters[2]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The y is not a float!", ex);
        }
        try {
            z = Float.parseFloat(whatMatters[3]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The z is not a float!", ex);
        }

        return new Position(world, x, y, z);
    }

    public static String serializeRotatedPosition(RotatedPosition rotatedPosition) {
        return "t/" + rotatedPosition.getPosition().getWorld() + "," + rotatedPosition.getPosition().getX() + "," + rotatedPosition.getPosition().getY() + "," + rotatedPosition.getPosition().getZ() + "," + rotatedPosition.getRotation().getX() + "," + rotatedPosition.getRotation().getY() + "," + rotatedPosition.getRotation().getY();
    }

    public static RotatedPosition deserializeRotatedPosition(String serialized) throws PropertyDeserializationException {
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

        String world = whatMatters[0];
        if (Bukkit.getWorld(world) == null) {
            throw new PropertyDeserializationException("The world '" + world + "' no longer exists!");
        }

        float x;
        float y;
        float z;
        float qx;
        float qy;
        try {
            x = Float.parseFloat(whatMatters[1]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The x is not a float!", ex);
        }
        try {
            y = Float.parseFloat(whatMatters[2]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The y is not a float!", ex);
        }
        try {
            z = Float.parseFloat(whatMatters[3]);
        } catch (NumberFormatException ex) {
            throw new PropertyDeserializationException("The z is not a float!", ex);
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

        return new RotatedPosition(new Position(world, x, y, z), new Vector2f(qx, qy));
    }

    private PropertySerializer() {
    }
}
