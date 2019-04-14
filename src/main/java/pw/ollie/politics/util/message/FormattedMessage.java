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
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public final class FormattedMessage {
    private final String message;

    public FormattedMessage(String message) {
        this.message = message;
    }

    public void send(CommandSender recipient) {
        recipient.sendMessage(message);
    }

    public void log(Plugin plugin, Level level) {
        plugin.getLogger().log(level, message);
    }

    public void log(Plugin plugin, Level level, Throwable e) {
        plugin.getLogger().log(level, message, e);
    }

    public String get() {
        return message;
    }
}
