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

import org.bukkit.ChatColor;

/**
 * Represents a messaging colour scheme.
 */
public final class ColourScheme {
    private final ChatColor normal;
    private final ChatColor error;
    private final ChatColor highlight;
    private final ChatColor prefix;

    /**
     * Constructs a new ColourScheme using the given {@link ChatColor}s.
     *
     * @param normal    the colour to use for normal text
     * @param error     the colour to use for error text
     * @param highlight the colour to use for highlighted text
     * @param prefix    the colour to use for prefix text
     */
    public ColourScheme(ChatColor normal, ChatColor error, ChatColor highlight, ChatColor prefix) {
        this.normal = normal;
        this.error = error;
        this.highlight = highlight;
        this.prefix = prefix;
    }

    /**
     * Gets the {@link ChatColor} used for normal text under this ColourScheme.
     *
     * @return the normal colour
     */
    public ChatColor getNormal() {
        return normal;
    }

    /**
     * Gets the {@link ChatColor} used for error text under this ColourScheme.
     *
     * @return the error colour
     */
    public ChatColor getError() {
        return error;
    }

    /**
     * Gets the {@link ChatColor} used for highlighted text under this ColourScheme.
     *
     * @return the highlight colour
     */
    public ChatColor getHighlight() {
        return highlight;
    }

    /**
     * Gets the {@link ChatColor} used for prefix text under this ColourScheme.
     *
     * @return the prefix colour
     */
    public ChatColor getPrefix() {
        return prefix;
    }

    public String transform(char prefix, String message) {
        return message.replace(prefix + "normal", normal.toString())
                .replace(prefix + "highlight", highlight.toString())
                .replace(prefix + "error", error.toString())
                .replace(prefix + "prefix", this.prefix.toString());
    }
}
