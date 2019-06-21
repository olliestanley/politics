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
package pw.ollie.politics.group.privilege;

import gnu.trove.map.hash.THashMap;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.util.FunctionalUtil;
import pw.ollie.politics.util.stream.CollectorUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Stores and provides access to {@link Privilege}s in Politics.
 */
public final class PrivilegeManager {
    // todo docs
    private final PoliticsPlugin plugin;
    private final Map<String, Privilege> privileges = new THashMap<>();

    public PrivilegeManager(PoliticsPlugin plugin) {
        this.plugin = plugin;

        loadDefaultPrivileges();
    }

    private void loadDefaultPrivileges() {
        registerPrivileges(Privileges.Group.ALL);
        registerPrivileges(Privileges.Plot.ALL);
        registerPrivileges(Privileges.GroupPlot.ALL);
    }

    /**
     * Attempts to register a single {@link Privilege}. This will not override existing Privileges with the same name.
     *
     * @param privilege the Privilege to register
     * @return whether the Privilege was successfully registered
     */
    public boolean registerPrivilege(Privilege privilege) {
        // putIfAbsent as we don't want other plugins overriding default Politics privileges
        return privileges.putIfAbsent(privilege.getName(), privilege) == null;
    }

    /**
     * Attempts to register multiple {@link Privileges}. Returns a {@link Set} of any Privileges which failed to be
     * registered.
     *
     * @param privileges the Privileges to attempt to register
     * @return a Set of any Privileges not successfully registered
     */
    public Set<Privilege> registerPrivileges(Privilege... privileges) {
        return Arrays.stream(privileges).filter(FunctionalUtil.negate(this::registerPrivilege)).collect(CollectorUtil.toTHashSet());
    }

    public Privilege getPrivilege(String name) {
        return privileges.get(name.toUpperCase().replaceAll(" ", "_"));
    }

    public PoliticsPlugin getPlugin() {
        return plugin;
    }
}
