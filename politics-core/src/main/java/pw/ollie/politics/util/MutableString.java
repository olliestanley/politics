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

import com.google.mu.util.stream.BiStream;

import java.util.Objects;

/**
 * Wrapper around a {@link String} which provides functionality equivalent to that of a mutable String, for usage in
 * lambdas.
 */
public final class MutableString {
    private String value;

    public MutableString(String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public String get() {
        return value;
    }

    public MutableString set(String value) {
        this.value = value;
        return this;
    }

    public MutableString replace(CharSequence target, CharSequence replacement) {
        return set(value.replace(target, replacement));
    }

    public MutableString replace(BiStream<? extends CharSequence, ? extends CharSequence> targetReplacementStream) {
        targetReplacementStream.forEach(this::replace);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MutableString)) {
            return false;
        }
        return o.toString().equals(value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
