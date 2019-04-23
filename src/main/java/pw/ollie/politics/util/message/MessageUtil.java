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

import org.bukkit.command.CommandSender;

public final class MessageUtil {
    public static MessageBuilder startBlockMessage(String heading) {
        return MessageBuilder.begin().prefix().append("========= ").highlight().append(heading).prefix().append(" =========").normal();
    }

    /**
     * Sends a formatted message with the plugin colour scheme and in normal colour.
     *
     * @param recipient the recipient of the message
     * @param message   the message to send
     */
    public static void message(CommandSender recipient, String message) {
        MessageBuilder.begin(message).send(recipient);
    }

    /**
     * Sends a formatted message with the plugin colour scheme and in error colour.
     *
     * @param recipient the recipient of the message
     * @param message   the message to send
     */
    public static void error(CommandSender recipient, String message) {
        MessageBuilder.beginError().append(message).send(recipient);
    }

    private MessageUtil() {
        throw new UnsupportedOperationException();
    }
}
