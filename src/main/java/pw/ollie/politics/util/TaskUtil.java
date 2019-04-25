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
package pw.ollie.politics.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * General utilities for running tasks through the Bukkit scheduler API.
 */
public final class TaskUtil {
    /**
     * Runs the given {@link Runnable} on a thread async to the main thread on the next tick.
     *
     * @param plugin   the {@link Plugin} to register the task through
     * @param runnable the task to run
     */
    public static void async(Plugin plugin, Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Runs the given {@link Runnable} on the main thread on the next tick.
     *
     * @param plugin   the {@link Plugin} to register the task through
     * @param runnable the task to run
     */
    public static void sync(Plugin plugin, Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(plugin);
    }

    private TaskUtil() {
        throw new UnsupportedOperationException();
    }
}
