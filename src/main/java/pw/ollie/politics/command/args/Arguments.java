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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple and easy to use method of parsing arguments into different primitive
 * types and parsing flags.
 */
public class Arguments {
    private final List<Argument> all;
    private final List<Argument> arguments;
    private final Set<Flag> flags;
    private final Set<Argument> doubleFlags;
    private final String[] raw;

    /**
     * Creates a new Arguments object and immediately parses the given String[]
     * of arguments into {@link Argument}s and {@link Flag}s.
     *
     * @param parse the raw argument {@link String}s to parse
     */
    public Arguments(String... parse) {
        this.all = new ArrayList<>(parse.length);
        this.arguments = new ArrayList<>(parse.length);
        this.flags = new HashSet<>();
        this.doubleFlags = new HashSet<>();
        this.raw = parse;

        for (int i = 0; i < raw.length; i++) {
            String arg = raw[i];
            boolean isLastArg = i == raw.length - 1;
            // construct a single Argument for the arg
            Argument sw = new Argument(arg);

            // add to the list of all arguments
            all.add(sw);

            boolean flag = arg.charAt(0) == '-';
            if (!flag || arg.length() < 2) {
                // normal argument, or flag with no name (e.g, "-")
                arguments.add(sw);
                continue;
            }

            boolean doubleFlag = arg.charAt(1) == '-';
            if (doubleFlag && arg.length() < 3) {
                // arg is "--" - no name given for flag
                arguments.add(sw);
                continue;
            }

            // flag argument handling
            if (doubleFlag) {
                // double flag (--, no value)
                doubleFlags.add(new Argument(
                        arg.substring(2)));
                continue;
            }

            if (isLastArg) {
                // single flag but no value given (this is the last arg)
                arguments.add(sw);
                continue;
            }

            // single flag (-, value)
            flags.add(new Flag(arg.substring(1),
                    new Argument(raw[++i])));
        }
    }

    public Argument get(int index) {
        return get(index, true);
    }

    public Argument get(int index, boolean includeFlagArgs) {
        if (includeFlagArgs) {
            return all.get(index);
        } else {
            return arguments.get(index);
        }
    }

    public String getString(int index) {
        return getString(index, true);
    }

    public String getString(int index, boolean includeFlagArgs) {
        return get(index, includeFlagArgs).get();
    }

    public Flag getValueFlag(String flag) {
        for (Flag f : flags) {
            if (f.getName().equalsIgnoreCase(flag)) {
                return f;
            }
        }
        return null;
    }

    public boolean hasValueFlag(String flag) {
        for (Flag f : flags) {
            if (f.getName().equalsIgnoreCase(flag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNonValueFlag(String flag) {
        for (Argument f : doubleFlags) {
            if (f.get().equalsIgnoreCase(flag)) {
                return true;
            }
        }
        return false;
    }

    public int length() {
        return length(true);
    }

    public int length(boolean includeFlagArgs) {
        if (includeFlagArgs) {
            return all.size();
        } else {
            return arguments.size();
        }
    }

    public Arguments subArgs(int startIndex, int endIndex) {
        if (startIndex == length()) {
            return new Arguments();
        }

        if (startIndex < 0 || endIndex >= raw.length) {
            throw new IllegalArgumentException("Array index out of bounds for Arguments#subArgs call");
        }

        String[] newRaw = Arrays.copyOfRange(raw, startIndex, endIndex, String[].class);
        return new Arguments(newRaw);
    }

    public String[] toStringArray() {
        String[] result = new String[raw.length];
        System.arraycopy(raw, 0, result, 0, raw.length);
        return result;
    }
}
