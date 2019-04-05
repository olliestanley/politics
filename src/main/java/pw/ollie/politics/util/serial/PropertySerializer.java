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

import pw.ollie.politics.util.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PropertySerializer {
    public static String serialize(Serializable obj) throws PropertySerializationException {
        try {
            return PropertySerializer.toString(obj);
        } catch (IOException e) {
            throw new PropertySerializationException("IOException occurred while serializing an object of type " + obj.getClass().getName() + "!", e);
        }
    }

    public static Object deserialize(String string) throws PropertyDeserializationException {
        try {
            return PropertySerializer.fromString(string);
        } catch (ClassNotFoundException e) {
            throw new PropertyDeserializationException("Could not find the object class for the given string while deserializing!", e);
        } catch (IOException e) {
            throw new PropertyDeserializationException("IOException occurred while deserializing a string!", e);
        }
    }

    private static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        byte[] byteArray = baos.toByteArray();
        return new String(Base64Coder.encode(byteArray, 0, byteArray.length));
    }

    private static Object fromString(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64Coder.decode(s.toCharArray(), 0, s.length());
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    private PropertySerializer() {
    }
}
