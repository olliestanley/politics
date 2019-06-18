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
package pw.ollie.politics.command;

import gnu.trove.map.hash.THashMap;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.group.GroupCommand;
import pw.ollie.politics.command.plot.PlotCommand;
import pw.ollie.politics.command.politics.PoliticsCommand;
import pw.ollie.politics.command.subplot.SubplotCommand;
import pw.ollie.politics.command.universe.UniverseCommand;
import pw.ollie.politics.group.level.GroupLevel;

import org.bukkit.command.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;

/**
 * Uses reflection to register commands to Bukkit without including them in plugin.yml. Needed as Politics command names
 * differ depending on the configuration.
 */
public final class PoliticsCommandManager {
    private final PoliticsPlugin plugin;
    private final Map<String, PoliticsBaseCommand> registered;

    public PoliticsCommandManager(PoliticsPlugin plugin) {
        this.plugin = plugin;
        this.registered = new THashMap<>();
    }

    public PoliticsBaseCommand getPoliticsCommand(String name) {
        return registered.get(name.toLowerCase());
    }

    /**
     * Must only be called once all internal plugin setup is complete.
     */
    public void registerCommands() {
        this.registerCommand(new PlotCommand(plugin));
        this.registerCommand(new PoliticsCommand(plugin));
        this.registerCommand(new SubplotCommand(plugin));
        this.registerCommand(new UniverseCommand(plugin));

        plugin.getUniverseManager().streamGroupLevels().forEach(this::registerGroupCommand);

        PoliticsCommandHelper.registerPermission(PoliticsCommandHelper.GROUPS_ADMIN_PERMISSION, "Allows performing functions, like land claiming, for groups you're not a member of");
        PoliticsCommandHelper.registerPermission(PoliticsCommandHelper.PLOTS_ADMIN_PERMISSION, "Allows performing functions for plots you don't own");
    }

    public void registerGroupCommand(GroupLevel level) {
        this.registerCommand(new GroupCommand(plugin, level));
    }

    private Object commandMap;
    private Method registerMethod;

    private void registerCommand(Command command) {
        if (command instanceof PoliticsBaseCommand) {
            registered.put(command.getName().toLowerCase(), (PoliticsBaseCommand) command);
        }

        if (commandMap == null) {
            try {
                Method commandMapGetter = plugin.getServer().getClass().getMethod("getCommandMap");
                commandMap = commandMapGetter.invoke(plugin.getServer());
            } catch (NoSuchMethodException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not find getCommandMap method. Politics is outdated or running on an unsupported platform. Commands will not function.", e);
                return;
            } catch (IllegalAccessException | InvocationTargetException e) {
                plugin.getLogger().log(Level.SEVERE, "Politics failed to register commands. Commands will not function.", e);
                return;
            }
        }

        if (registerMethod == null) {
            try {
                registerMethod = commandMap.getClass().getMethod("register", String.class, Command.class);
            } catch (NoSuchMethodException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not find register method. Politics is outdated or running on an unsupported platform. Commands will not function.", e);
                return;
            }
        }

        try {
            registerMethod.invoke(commandMap, command.getName(), command);
        } catch (IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().log(Level.SEVERE, "Politics failed to register command '" + command.getName() + "'. Command will not function.", e);
        }
    }
}
