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

import pw.ollie.politics.PoliticsPlugin;

import java.io.File;

public final class PoliticsFileSystem {
    private final File baseDir;
    private final File rulesDir;
    private final File universesDir;
    private final File worldConfigDir;
    private final File worldsDir;

    public PoliticsFileSystem(PoliticsPlugin plugin) {
        this.baseDir = plugin.getDataFolder();
        this.rulesDir = new File(baseDir, "rules/");
        this.universesDir = new File(baseDir, "data/universes/");
        this.worldConfigDir = new File(baseDir, "worlds/");
        this.worldsDir = new File(baseDir, "data/worlds/");

        this.createDirectories();
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getRulesDir() {
        return rulesDir;
    }

    public File getWorldConfigDir() {
        return worldConfigDir;
    }

    public File getUniversesDir() {
        return universesDir;
    }

    public File getWorldsDir() {
        return worldsDir;
    }

    private void createDirectories() {
        if (!(this.rulesDir.exists())) {
            this.rulesDir.mkdirs();
        }
        if (!this.worldConfigDir.exists()) {
            this.worldConfigDir.mkdirs();
        }
        if (!this.worldsDir.exists()) {
            this.worldsDir.mkdirs();
        }
        if (!this.universesDir.exists()) {
            this.universesDir.mkdirs();
        }
    }
}
