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
package pw.ollie.politics.world.plot;

import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.group.privilege.Privileges;

/**
 * Represents a type of protection for a {@link Plot} or {@link Subplot}.
 */
public enum PlotProtectionType {
    /**
     * A protection from a block being broken.
     */
    BLOCK_BREAK(Privileges.GroupPlot.BUILD),
    /**
     * A protection from a block being placed.
     */
    BLOCK_PLACE(Privileges.GroupPlot.BUILD),
    /**
     * A protection from a piston pulling a block.
     */
    PISTON_PULL(),
    /**
     * A protection from a piston pushing a block.
     */
    PISTON_PUSH(),
    /**
     * A protection from a dispenser releasing a block.
     */
    DISPENSER(),
    /**
     * A protection from a block flowing naturally.
     */
    BLOCK_FLOW();
    // todo fill in all types

    private final Privilege permission;

    PlotProtectionType() {
        this(null);
    }

    PlotProtectionType(Privilege permission) {
        this.permission = permission;
    }

    /**
     * Gets the {@link Privilege} which would allow the protection to be bypassed.
     * <p>
     * If there isn't a relevant permission, this returns null.
     *
     * @return the Privilege required to avoid this protection type, or {@code null} if there isn't one
     */
    public Privilege getPermission() {
        return permission;
    }
}
