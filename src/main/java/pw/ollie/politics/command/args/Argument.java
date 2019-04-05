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
package pw.ollie.politics.command.args;

/**
 * A wrapper around a {@link String} which allows for parsing of many primitive
 * data types as well as providing methods to check whether the argument is a
 * valid form of said primitive types.
 * <p>
 * Argument objects are immutable and any methods which may appear to make
 * a modification(s) to the state of the Argument will return a new object.
 */
public final class Argument {
    private final String raw;

    public Argument(String arg) {
        if (arg == null) {
            throw new IllegalArgumentException();
        }
        this.raw = arg;
    }

    public String get() {
        return raw;
    }

    public int asInt() {
        return Integer.parseInt(raw);
    }

    public double asDouble() {
        return Double.parseDouble(raw);
    }

    public float asFloat() {
        return Float.parseFloat(raw);
    }

    public long asLong() {
        return Long.parseLong(raw);
    }

    public short asShort() {
        return Short.parseShort(raw);
    }

    public Boolean asBoolean() {
        return Boolean.valueOf(raw);
    }

    public Character asChar() {
        return raw.length() == 1 ? raw.charAt(0) : null;
    }

    public boolean isInt() {
        try {
            asInt();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isDouble() {
        try {
            asDouble();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isFloat() {
        try {
            asFloat();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isLong() {
        try {
            asLong();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isShort() {
        try {
            asShort();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isBoolean() {
        return raw.equals("true") || raw.equals("false");
    }

    public boolean isChar() {
        return raw.length() == 1;
    }

    public String getIntern() {
        return raw.intern();
    }

    public Argument concat(String string) {
        return new Argument(raw.concat(string));
    }

    public Argument substring(int startIndex, int endIndex) {
        return new Argument(raw.substring(startIndex, endIndex));
    }

    public Argument toLowerCase() {
        return new Argument(raw.toLowerCase());
    }

    public Argument toUpperCase() {
        return new Argument(raw.toUpperCase());
    }

    public char[] toCharArray() {
        return raw.toCharArray();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Argument && ((Argument) other).raw
                .equals(raw);
    }

    @Override
    public int hashCode() {
        return raw.hashCode();
    }

    @Override
    public String toString() {
        return raw.intern();
    }
}
