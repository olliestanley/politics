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
package pw.ollie.politicswar.war;

import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.Politics;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.serial.FileUtil;
import pw.ollie.politicswar.PoliticsWarPlugin;
import pw.ollie.politicswar.event.PoliticsWarEventFactory;
import pw.ollie.politicswar.event.war.WarBeginEvent;
import pw.ollie.politicswar.event.war.WarFinishEvent;

import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Stores data on and provides access to {@link War}s in Politics.
 */
public final class WarManager {
    // todo docs
    private final PoliticsWarPlugin plugin;
    private final Set<War> activeWars;

    public WarManager(PoliticsWarPlugin plugin) {
        this.plugin = plugin;
        this.activeWars = new THashSet<>();
    }

    public Set<War> getActiveWars() {
        return new THashSet<>(activeWars);
    }

    public Set<War> getActiveWars(Universe universe) {
        return activeWars.stream()
                .filter(war -> war.getUniverse().equals(universe))
                .collect(Collectors.toSet());
    }

    public War getWarBetween(int one, int two) {
        return activeWars.stream()
                .filter(war -> war.involves(one) && war.involves(two))
                .findAny().orElse(null);
    }

    public War getWarBetween(Group group1, Group group2) {
        return getWarBetween(group1.getUid(), group2.getUid());
    }

    public Set<War> getInvolvedWars(int groupId) {
        return activeWars.stream()
                .filter(war -> war.involves(groupId))
                .collect(Collectors.toSet());
    }

    public Set<War> getInvolvedWars(Group group) {
        return getInvolvedWars(group.getUid());
    }

    public boolean beginWar(War war) {
        return beginWar(war, Bukkit.getConsoleSender());
    }

    public boolean beginWar(War war, CommandSender source) {
        if (war.isActive() || activeWars.contains(war) || getWarBetween(war.getAggressorId(), war.getDefenderId()) != null) {
            return false;
        }

        Group defender = war.getDefender();
        Group aggressor = war.getAggressor();
        if (defender == null || aggressor == null) {
            throw new IllegalArgumentException("cannot start war with one or more null participants");
        }
        if (!aggressor.getLevel().equals(defender.getLevel())) {
            throw new IllegalArgumentException("cannot start war between groups of different levels");
        }

        if (!aggressor.getLevel().canWar()) {
            return false;
        }
        if (aggressor.getBooleanProperty(GroupProperty.PEACEFUL)
                || defender.getBooleanProperty(GroupProperty.PEACEFUL)) {
            return false;
        }

        WarBeginEvent event = PoliticsWarEventFactory.callWarBeginEvent(war, source);
        if (event.isCancelled()) {
            return false;
        }

        war.setActive(true);
        activeWars.add(war);
        return true;
    }

    public boolean finishWar(War war) {
        return finishWar(war, false);
    }

    public boolean finishWar(War war, boolean force) {
        if (!war.isActive() || !activeWars.contains(war)) {
            return false;
        }

        WarFinishEvent event = PoliticsWarEventFactory.callWarFinishEvent(war);
        if (!force && event.isCancelled()) {
            return false;
        }

        // update victory and defeat statistics for involved groups
        Group winner = war.getWinningGroup();
        winner.setProperty(GroupProperty.WAR_VICTORIES, winner.getIntProperty(GroupProperty.WAR_VICTORIES, 0) + 1);
        Group loser = war.getLosingGroup();
        loser.setProperty(GroupProperty.WAR_DEFEATS, winner.getIntProperty(GroupProperty.WAR_DEFEATS, 0) + 1);

        war.setActive(false);
        activeWars.remove(war);
        return true;
    }

    public void loadWars() {
        File dataDir = Politics.getFileSystem().getDataDir();
        File warsDataDir = new File(dataDir, "wars");
        BSONDecoder decoder = new BasicBSONDecoder();

        for (File file : Objects.requireNonNull(warsDataDir.listFiles())) {
            String fileName = file.getName();
            if (!fileName.endsWith(".pws") || fileName.length() <= 4) {
                continue;
            }

            byte[] data;
            try {
                data = Files.readAllBytes(file.toPath());
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not read war file `" + fileName + "'!", ex);
                continue;
            }

            BSONObject bson = decoder.readObject(data);
            if (!(bson instanceof BasicBSONObject)) {
                plugin.getLogger().log(Level.SEVERE, "Could not read war file + '" + fileName + "'!");
                continue;
            }

            War war = new War((BasicBSONObject) bson);
            activeWars.add(war);
        }

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(new WarProtectionListener(plugin), plugin);
        pluginManager.registerEvents(new WarScoringListener(plugin), plugin);
    }

    public void saveWars() {
        File dataDir = Politics.getFileSystem().getDataDir();
        File warsDataDir = new File(dataDir, "wars");
        BSONEncoder encoder = new BasicBSONEncoder();

        Set<String> storedWarFiles = new HashSet<>();

        for (War war : activeWars) {
            if (!war.canStore()) {
                continue;
            }

            String fileName = war.getAggressorId() + "_" + war.getDefenderId() + ".pws";
            File warFile = new File(warsDataDir, fileName);
            storedWarFiles.add(warFile.getAbsolutePath());

            try {
                FileUtil.createBackup(warFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not back up world file for war. Data will not be saved...", ex);
                continue;
            }

            byte[] data = encoder.encode(war.toBSONObject());
            try {
                Files.write(warFile.toPath(), data);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save war file due to error! Please restore backup...", ex);
            }
        }

        for (File file : Objects.requireNonNull(warsDataDir.listFiles())) {
            if (!storedWarFiles.contains(file.getAbsolutePath())) {
                file.delete();
            }
        }
    }
}
