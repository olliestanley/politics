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
package pw.ollie.politics.data;

/**
 * Thrown when a configuration file in Politics is invalid.
 */
public class InvalidConfigurationException extends Exception {
    private static final long serialVersionUID = -3792841272998195396L;

    public InvalidConfigurationException(String reason) {
        super(reason);
    }

    public InvalidConfigurationException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
