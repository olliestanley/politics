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
package net.oliverstanley.politics.command;

import net.oliverstanley.politics.PoliticsPlugin;
import net.oliverstanley.politics.group.GroupLevel;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PoliticsCommandRegistrar {
    private final PoliticsPlugin plugin;
    private final Set<String> registeredLevels;

    private BukkitCommandManager acfManager;

    public PoliticsCommandRegistrar(PoliticsPlugin plugin) {
        this.plugin = plugin;
        this.registeredLevels = new HashSet<>();
    }

    public void registerCommands() {
        this.acfManager = new BukkitCommandManager(this.plugin);
        this.acfManager.enableUnstableAPI("help");

        this.acfManager.registerCommand(new CommandPlot());
        this.acfManager.registerCommand(new CommandPolitics());
        this.acfManager.registerCommand(new CommandSubplot());
        this.acfManager.registerCommand(new CommandUniverse());

        this.plugin.getUniverseHandler().groupLevels().forEach(this::registerGroupCommand);
    }

    public void registerGroupCommand(@NotNull GroupLevel level) {
        if (this.registeredLevels.contains(level.getId()))
            return;

        this.acfManager.registerCommand(configureGroupCommand(level));
        this.registeredLevels.add(level.getId());
    }

    private static final Field commandNameField;

    static {
        try {
            commandNameField = BaseCommand.class.getDeclaredField("commandName");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static CommandGroup configureGroupCommand(@NotNull GroupLevel level) {
        Objects.requireNonNull(commandNameField);

        CommandGroup command = new CommandGroup(level);

        try {
            commandNameField.set(command, level.getId());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return command;
    }
}
