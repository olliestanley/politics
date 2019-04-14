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
package pw.ollie.politics.util.message;

import pw.ollie.politics.Politics;

public final class MessageBuilder {
    private final StringBuilder delegate;
    private final ColourScheme colourScheme;

    public MessageBuilder(ColourScheme colourScheme) {
        this.delegate = new StringBuilder();
        this.colourScheme = colourScheme;

        normal();
    }

    public MessageBuilder append(String content) {
        delegate.append(content);
        return this;
    }

    public MessageBuilder normal() {
        delegate.append(colourScheme.getNormal());
        return this;
    }

    public MessageBuilder error() {
        delegate.append(colourScheme.getError());
        return this;
    }

    public MessageBuilder highlight() {
        delegate.append(colourScheme.getHighlight());
        return this;
    }

    public MessageBuilder prefix() {
        delegate.append(colourScheme.getPrefix());
        return this;
    }

    public MessageBuilder newLine() {
        delegate.append("\n");
        return this;
    }

    public FormattedMessage build() {
        return new FormattedMessage(delegate.toString());
    }

    public static MessageBuilder begin(ColourScheme colourScheme) {
        return new MessageBuilder(colourScheme);
    }

    public static MessageBuilder begin(ColourScheme colourScheme, String initial) {
        return begin(colourScheme).append(initial);
    }

    public static MessageBuilder begin() {
        return begin(Politics.getColourScheme());
    }

    public static MessageBuilder begin(String initial) {
        return begin().append(initial);
    }

    public static MessageBuilder beginError() {
        return begin().error();
    }
}
