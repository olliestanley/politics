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

import org.bukkit.plugin.java.JavaPlugin;

import pw.ollie.politics.data.PoliticsFileSystem;
import pw.ollie.politics.universe.UniverseManager;

public final class PoliticsPlugin extends JavaPlugin {
    private PoliticsFileSystem fileSystem;
    private UniverseManager universeManager;

    @Override
    public void onEnable() {
        this.fileSystem = new PoliticsFileSystem(this);
        this.universeManager = new UniverseManager(this);
    }

    @Override
    public void onDisable() {
    }

    public PoliticsFileSystem getFileSystem() {
        return this.fileSystem;
    }

    public UniverseManager getUniverseManager() {
        return this.universeManager;
    }
}
