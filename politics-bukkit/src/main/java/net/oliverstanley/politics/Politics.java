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
package net.oliverstanley.politics;

import net.oliverstanley.politics.data.PoliticsFileSystem;
import net.oliverstanley.politics.group.Group;
import net.oliverstanley.politics.privilege.PrivilegeHandler;
import net.oliverstanley.politics.universe.UniverseHandler;
import net.oliverstanley.politics.world.PoliticsWorld;
import net.oliverstanley.politics.world.WorldHandler;

import org.bukkit.Server;
import org.bukkit.World;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Static access methods for Politics' managers and other objects. Note that some methods may return null if the
 * relevant aspect of Politics is configured to be disabled.
 * <p>
 * Most methods may also throw {@link NullPointerException} if called while Politics is disabled.
 */
public final class Politics {
    /**
     * Gets the {@link PoliticsPlugin} instance.
     *
     * @return the running plugin instance
     */
    public static PoliticsPlugin getPlugin() {
        return PoliticsPlugin.instance();
    }

    /**
     * Gets the Bukkit {@link Server} the current plugin instance is associated with.
     *
     * @return current plugin Server instance
     */
    public static Server getServer() {
        return Politics.getPlugin().getServer();
    }

    /**
     * Gets the {@link PoliticsFileSystem} of the plugin. This provides access to the directories Politics stores data
     * and configuration in.
     *
     * @return the PoliticsFileSystem object associated with the current plugin instance
     */
    public static PoliticsFileSystem getFileSystem() {
        return Politics.getPlugin().getFileSystem();
    }

    /**
     * Gets the {@link PrivilegeHandler} associated with the current plugin instance.
     *
     * @return current plugin PrivilegeHandler instance
     */
    public static PrivilegeHandler getPrivilegeManager() {
        return Politics.getPlugin().getPrivilegeHandler();
    }

    /**
     * Gets the {@link WorldHandler} associated with the current plugin instance.
     *
     * @return current plugin WorldHandler instance
     */
    public static WorldHandler getWorldManager() {
        return Politics.getPlugin().getWorldHandler();
    }

    /**
     * Gets the {@link UniverseHandler} associated with the current plugin instance.
     *
     * @return current plugin UniverseHandler instance
     */
    public static UniverseHandler getUniverseManager() {
        return Politics.getPlugin().getUniverseHandler();
    }

    /**
     * Gets the relevant {@link PoliticsWorld} object for the given {@link World}.
     *
     * @param world the Bukkit World to get the PoliticsWorld associated with
     * @return the relevant PoliticsWorld for the given World
     */
    public static PoliticsWorld getWorld(World world) {
        return Politics.getWorldManager().getWorld(world);
    }

    /**
     * Gets the relevant {@link PoliticsWorld} object for the given world name.
     *
     * @param world the name of the world to get the PoliticsWorld associated with
     * @return the relevant PoliticsWorld for the given world name
     */
    public static PoliticsWorld getWorld(String world) {
        return Politics.getWorldManager().getWorld(world);
    }

    /**
     * Gets the {@link Group} with the given unique id.
     * <p>
     * Will return {@code null} if there is no Group with that id.
     *
     * @param id the id of the Group
     * @return the relevant Group for the given id
     */
    public static Optional<Group> getGroupById(int id) {
        return Politics.getUniverseManager().getGroupById(id);
    }

    /**
     * Gets the current plugin {@link Logger} instance.
     *
     * @return current plugin Logger instance
     */
    public static Logger getLogger() {
        return Politics.getPlugin().getLogger();
    }

    private Politics() {
        throw new UnsupportedOperationException();
    }
}
