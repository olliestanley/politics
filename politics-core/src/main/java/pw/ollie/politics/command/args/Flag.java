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
 * A flag which simply has a name and a value.
 */
public final class Flag {
    private final String name;
    private final Argument valArg;

    /**
     * Constructs a new {@link Flag} with the given name and the given value.
     *
     * @param name  the name of the flag
     * @param value the value for the flag
     */
    public Flag(String name, Argument value) {
        this.name = name;
        this.valArg = value;
    }

    /**
     * Gets the name of this flag - for example, 'f' in '-f trees'.
     *
     * @return this flag's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the {@link Argument} which represents the value provided for  this flag.
     *
     * @return this flag's value
     */
    public Argument getValue() {
        return valArg;
    }


    /**
     * Gets the raw {@link String} value which was provided for this flag. For example, 'trees' in '-f trees'.
     *
     * @return this flag's raw String value
     */
    public String getStringValue() {
        return getValue().get();
    }
}
