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
package pw.ollie.politics;

import pw.ollie.politics.activity.ActivityManager;
import pw.ollie.politics.command.PoliticsCommandManager;
import pw.ollie.politics.data.PoliticsFileSystem;
import pw.ollie.politics.economy.PoliticsEconomy;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupManager;
import pw.ollie.politics.group.privilege.PrivilegeManager;
import pw.ollie.politics.universe.UniverseManager;
import pw.ollie.politics.util.message.ColourScheme;
import pw.ollie.politics.util.message.Messenger;
import pw.ollie.politics.util.message.Notifier;
import pw.ollie.politics.util.visualise.Visualiser;
import pw.ollie.politics.world.PoliticsWorld;
import pw.ollie.politics.world.WorldManager;

import com.google.mu.util.stream.BiStream;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Map;
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
     * Gets the {@link PoliticsConfig} instance which provides access to general plugin configuration values.
     *
     * @return current plugin PoliticsConfig instance
     */
    public static PoliticsConfig getConfig() {
        return Politics.getPlugin().getPoliticsConfig();
    }

    /**
     * Gets the {@link Messenger} instance which is used for handling configurable messages in Politics.
     *
     * @return the plugin Messenger instance
     */
    public static Messenger getMessenger() {
        return Politics.getPlugin().getMessenger();
    }

    public static void sendConfiguredMessage(CommandSender recipient, String key) {
        Politics.getMessenger().sendConfiguredMessage(recipient, key);
    }

    public static void sendConfiguredMessage(CommandSender recipient, String key, BiStream<String, String> vars) {
        Politics.getMessenger().sendConfiguredMessage(recipient, key, vars);
    }

    public static void sendConfiguredMessage(CommandSender recipient, String key, Map<String, String> vars) {
        Politics.getMessenger().sendConfiguredMessage(recipient, key, vars);
    }

    /**
     * Gets the {@link Notifier} instance.
     *
     * @return the plugin Notifier instance
     */
    public static Notifier getNotifier() {
        return Politics.getPlugin().getNotifier();
    }

    /**
     * Gets the {@link ColourScheme} in use by Politics for messaging.
     *
     * @return the used ColourScheme
     */
    public static ColourScheme getColourScheme() {
        return Politics.getConfig().getColourScheme();
    }

    /**
     * Gets the {@link PrivilegeManager} associated with the current plugin instance.
     *
     * @return current plugin PrivilegeManager instance
     */
    public static PrivilegeManager getPrivilegeManager() {
        return Politics.getPlugin().getPrivilegeManager();
    }

    /**
     * Gets the {@link WorldManager} associated with the current plugin instance.
     *
     * @return current plugin WorldManager instance
     */
    public static WorldManager getWorldManager() {
        return Politics.getPlugin().getWorldManager();
    }

    /**
     * Gets the {@link UniverseManager} associated with the current plugin instance.
     *
     * @return current plugin UniverseManager instance
     */
    public static UniverseManager getUniverseManager() {
        return Politics.getPlugin().getUniverseManager();
    }

    /**
     * Gets the {@link GroupManager} associated with the current plugin instance.
     *
     * @return current plugin GroupManager instance
     */
    public static GroupManager getGroupManager() {
        return Politics.getPlugin().getGroupManager();
    }

    /**
     * Gets the {@link ActivityManager} associated with the current plugin instance.
     *
     * @return current plugin ActivityManager instance
     */
    public static ActivityManager getActivityManager() {
        return Politics.getPlugin().getActivityManager();
    }

    /**
     * Gets the {@link PoliticsEconomy} associated with the current plugin instance.
     * <p>
     * Will return null if the plugin is not configured to enable economy features.
     *
     * @return current plugin PoliticsEconomy instance, or {@code null} if economy features are disabled
     */
    public static PoliticsEconomy getPoliticsEconomy() {
        return Politics.getPlugin().getEconomy();
    }

    /**
     * Gets the {@link PoliticsCommandManager} associated with the current plugin instance.
     *
     * @return current plugin PoliticsCommandManager instance
     */
    public static PoliticsCommandManager getCommandManager() {
        return Politics.getPlugin().getCommandManager();
    }

    /**
     * Gets the {@link Visualiser} associated with the current plugin instance.
     *
     * @return current plugin Visualiser instance
     */
    public static Visualiser getVisualiser() {
        return Politics.getPlugin().getVisualiser();
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
