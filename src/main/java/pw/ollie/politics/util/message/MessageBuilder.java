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

import org.bukkit.command.CommandSender;

/**
 * Builds {@link FormattedMessage} objects using a set {@link ColourScheme}.
 */
public final class MessageBuilder {
    private final StringBuilder delegate;
    private final ColourScheme colourScheme;

    /**
     * Constructs a new MessageBuilder with the given {@link ColourScheme} in normal text colour.
     *
     * @param colourScheme the ColourScheme for the MessageBuilder to use
     */
    private MessageBuilder(ColourScheme colourScheme) {
        this.delegate = new StringBuilder();
        this.colourScheme = colourScheme;

        normal();
    }

    /**
     * Appends the given text to this MessageBuilder.
     *
     * @param content the text to append
     * @return this MessageBuilder object
     */
    public MessageBuilder append(String content) {
        delegate.append(content);
        return this;
    }

    /**
     * Switches to normal text colour.
     *
     * @return this MessageBuilder object
     */
    public MessageBuilder normal() {
        delegate.append(colourScheme.getNormal());
        return this;
    }

    /**
     * Switches to normal text colour, then appends the given text to this MessageBuilder.
     *
     * @param content the text to append
     * @return this MessageBuilder object
     */
    public MessageBuilder normal(String content) {
        return normal().append(content);
    }

    /**
     * Switches to error text colour.
     *
     * @return this MessageBuilder object
     */
    public MessageBuilder error() {
        delegate.append(colourScheme.getError());
        return this;
    }

    /**
     * Switches to error text colour, then appends the given text to this MessageBuilder.
     *
     * @param content the error text to append
     * @return this MessageBuilder object
     */
    public MessageBuilder error(String content) {
        return error().append(content);
    }

    /**
     * Switches to highlighted text colour.
     *
     * @return this MessageBuilder object
     */
    public MessageBuilder highlight() {
        delegate.append(colourScheme.getHighlight());
        return this;
    }

    /**
     * Switches to highlighted text colour, then appends the given text to this MessageBuilder.
     *
     * @param content the highlighted text to append
     * @return this MessageBuilder object
     */
    public MessageBuilder highlight(String content) {
        return highlight().append(content);
    }

    /**
     * Switches to prefix text colour.
     *
     * @return this MessageBuilder object
     */
    public MessageBuilder prefix() {
        delegate.append(colourScheme.getPrefix());
        return this;
    }

    /**
     * Switches to prefix text colour, then appends the given text to this MessageBuilder.
     *
     * @param content the prefix text to append
     * @return this MessageBuilder object
     */
    public MessageBuilder prefix(String content) {
        return prefix().append(content);
    }

    /**
     * Appends a line break to this MessageBuilder.
     *
     * @return this MessageBuilder object
     */
    public MessageBuilder newLine() {
        delegate.append("\n");
        return this;
    }

    /**
     * Builds all of the appended content into a {@link FormattedMessage} object.
     *
     * @return a FormattedMessage of the content appended to this builder
     */
    public FormattedMessage build() {
        return new FormattedMessage(delegate.toString());
    }

    /**
     * Sends the current content to the given recipient.
     *
     * @param recipient the recipient of the message
     */
    public void send(CommandSender recipient) {
        build().send(recipient);
    }

    /**
     * Begins a new message in normal colour of the given {@link ColourScheme}.
     *
     * @param colourScheme the ColourScheme to be used for the new MessageBuilder
     * @return a new MessageBuilder set to normal colour of the given ColourScheme
     */
    public static MessageBuilder begin(ColourScheme colourScheme) {
        return new MessageBuilder(colourScheme);
    }

    /**
     * Begins a new message in normal colour of the given {@link ColourScheme} with the given initial text.
     *
     * @param colourScheme the ColourScheme to be used for the new MessageBuilder
     * @param initial      the initial text to append to the MessageBuilder
     * @return a new MessageBuilder set to normal colour of the given ColourScheme with given text
     */
    public static MessageBuilder begin(ColourScheme colourScheme, String initial) {
        return begin(colourScheme).append(initial);
    }

    /**
     * Begins a new message in normal colour of the default Politics {@link ColourScheme}.
     *
     * @return a new MessageBuilder set to the default normal colour
     */
    public static MessageBuilder begin() {
        return begin(Politics.getColourScheme());
    }

    /**
     * Begins a new message in normal colour of the default Politics {@link ColourScheme} with the given starting text.
     *
     * @param initial the initial text for the MessageBuilder
     * @return a new MessageBuilder set to the default normal colour with initial text
     */
    public static MessageBuilder begin(String initial) {
        return begin().append(initial);
    }

    /**
     * Begins a new message in error colour of the default Politics {@link ColourScheme}.
     *
     * @return a new MessageBuilder set to the default error colour
     */
    public static MessageBuilder beginError() {
        return begin().error();
    }

    /**
     * Begins a new message in error colour of the default Politics {@link ColourScheme}, with given initial text.
     *
     * @param initial the initial text to append to the MessageBuilder
     * @return a new MessageBuilder set to the default error colour
     */
    public static MessageBuilder beginError(String initial) {
        return begin().error(initial);
    }

    /**
     * Begins a new message in highlight colour of the default Politics {@link ColourScheme}.
     *
     * @return a new MessageBuilder set to the default highlight colour
     */
    public static MessageBuilder beginHighlight() {
        return begin().highlight();
    }

    /**
     * Begins a new message in highlight colour of the default Politics {@link ColourScheme}, with given initial text.
     *
     * @param initial the initial text to append to the MessageBuilder
     * @return a new MessageBuilder set to the default highlight colour
     */
    public static MessageBuilder beginHighlight(String initial) {
        return begin().highlight(initial);
    }
}
