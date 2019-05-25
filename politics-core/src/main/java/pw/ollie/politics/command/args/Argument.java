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
    /**
     * The raw string for the argument wrapped by this Argument object.
     */
    private final String raw;

    /**
     * Creates a new Argument, using the given {@link String} argument as a raw String.
     *
     * @param arg the raw String for this Argument
     */
    public Argument(String arg) {
        if (arg == null) {
            throw new IllegalArgumentException();
        }
        this.raw = arg;
    }

    /**
     * Gets the raw {@link String} this Argument wraps.
     *
     * @return this Argument's raw String value
     */
    public String get() {
        return raw;
    }

    /**
     * Returns this Argument's value parsed as an int.
     *
     * @return this Argument's value parsed as an int
     * @throws NumberFormatException if the value isn't an int
     */
    public int asInt() {
        return Integer.parseInt(raw);
    }

    /**
     * Returns this Argument's value parsed as a double.
     *
     * @return this Argument's value parsed as a double
     * @throws NumberFormatException if the value isn't a double
     */
    public double asDouble() {
        return Double.parseDouble(raw);
    }

    /**
     * Returns this Argument's value parsed as a float.
     *
     * @return this Argument's value parsed as a float
     * @throws NumberFormatException if the argument isn't a float
     */
    public float asFloat() {
        return Float.parseFloat(raw);
    }

    /**
     * Returns this Argument's value parsed as a long.
     *
     * @return this Argument's value parsed as a long
     * @throws NumberFormatException if the value isn't a long
     */
    public long asLong() {
        return Long.parseLong(raw);
    }

    /**
     * Returns this Argument's value parsed as a short.
     *
     * @return this Argument's value parsed as a short
     * @throws NumberFormatException if the value isn't a short
     */
    public short asShort() {
        return Short.parseShort(raw);
    }

    /**
     * Returns this Argument's value parsed as a boolean.
     *
     * @return this Argument's value parsed as a boolean
     */
    public Boolean asBoolean() {
        return Boolean.valueOf(raw);
    }

    /**
     * Returns this Argument's value parsed as a Character. {@code null} is returned if the raw string is not one
     * character long.
     *
     * @return this Argument's value parsed as a Character
     */
    public Character asChar() {
        return raw.length() == 1 ? raw.charAt(0) : null;
    }

    /**
     * Checks whether this Argument's value can be parsed as an integer.
     *
     * @return whether this Argument's value can be parsed as an integer
     */
    public boolean isInt() {
        try {
            asInt();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks whether this Argument's value can be parsed as a double.
     *
     * @return whether this Argument's value can be parsed as a double
     */
    public boolean isDouble() {
        try {
            asDouble();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks whether this Argument's value can be parsed as a float.
     *
     * @return whether this Argument's value can be parsed as a float
     */
    public boolean isFloat() {
        try {
            asFloat();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks whether this Argument's value can be parsed as a long.
     *
     * @return whether this Argument's value can be parsed as a long
     */
    public boolean isLong() {
        try {
            asLong();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks whether this Argument's value can be parsed as a short.
     *
     * @return whether this Argument's value can be parsed as a short
     */
    public boolean isShort() {
        try {
            asShort();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks whether this Argument's value can be parsed as a boolean.
     *
     * @return whether this Argument's value can be parsed as a boolean
     */
    public boolean isBoolean() {
        return raw.equals("true") || raw.equals("false");
    }

    /**
     * Checks whether this Argument's value can be parsed as a char.
     *
     * @return whether this Argument's value can be parsed as a char
     */
    public boolean isChar() {
        return raw.length() == 1;
    }

    /**
     * @return {@code raw.intern()}
     */
    public String getIntern() {
        return raw.intern();
    }

    /**
     * Concatenate the given {@link String} onto this Argument.
     *
     * @param string the string to add to the end of the current string
     * @return {@code new Argument(raw.concat(string))}
     */
    public Argument concat(String string) {
        return new Argument(raw.concat(string));
    }

    /**
     * Gets a sub-string between the given indices in Argument form.
     *
     * @param startIndex the start of the substring
     * @param endIndex   the end of the substring
     * @return {@code new Argument(raw.substring(startIndex, endIndex))}
     */
    public Argument substring(int startIndex, int endIndex) {
        return new Argument(raw.substring(startIndex, endIndex));
    }

    /**
     * Gets a lower-case version of this Argument.
     *
     * @return {@code new Argument(raw.toLowerCase())}
     */
    public Argument toLowerCase() {
        return new Argument(raw.toLowerCase());
    }

    /**
     * Gets an upper-case version of this Argument.
     *
     * @return {@code new Argument(raw.toUpperCase())}
     */
    public Argument toUpperCase() {
        return new Argument(raw.toUpperCase());
    }

    /**
     * Gets the char[] associated with the raw {@link String} for this Argument.
     *
     * @return {@code raw.toCharArray()}
     */
    public char[] toCharArray() {
        return raw.toCharArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Argument && ((Argument) other).raw
                .equals(raw);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return raw.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return raw.intern();
    }
}
