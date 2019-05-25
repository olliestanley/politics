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
package pw.ollie.politicstax.util;

import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.universe.UniverseRules;
import pw.ollie.politics.world.WorldConfig;
import pw.ollie.politicstax.AbstractPoliticsTaxTest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class PoliticsTaxTestObjectFactory {
    public static WorldConfig newDefaultWorldConfig() {
        return PoliticsTaxTestReflection.instantiateWorldConfig(AbstractPoliticsTaxTest.TEST_WORLD_NAME, true, true);
    }

    public static UniverseRules newDefaultUniverseRules() {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("description", "Testing configuration for Politics");
        config.set("wilderness-message", "Wilderness");

        ConfigurationSection wars = config.createSection("wars");
        wars.set("enabled", true);

        ConfigurationSection levels = config.createSection("levels");
        {
            ConfigurationSection household = levels.createSection("household");
            household.set("name", "Household");
            household.set("plural", "Households");
            household.set("rank", 1);
            household.set("children", new ArrayList<String>());

            ConfigurationSection roles = household.createSection("roles");
            {
                ConfigurationSection owner = roles.createSection("Owner");
                owner.set("name", "Owner");
                owner.set("plural", "Owners");
                owner.set("privileges", stringList(Privileges.all()));
            }
            {
                ConfigurationSection member = roles.createSection("Member");
                member.set("name", "Member");
                member.set("plural", "Members");
                member.set("privileges", Collections.singletonList(Privileges.GroupPlot.BUILD.getName()));
            }

            household.set("initial", "Member");
            household.set("founder", "Owner");
            household.set("friendly-fire", false);
            household.set("has-immediate-members", true);
            household.set("can-own-land", true);
            household.set("allowed-multiple", false);
            household.set("can-war", false);
            household.set("may-be-peaceful", true);
            household.set("can-tax", false);
        }

        {
            ConfigurationSection town = levels.createSection("town");
            town.set("name", "Town");
            town.set("plural", "Towns");
            town.set("rank", 2);
            town.set("children", Collections.singletonList("Household"));

            ConfigurationSection roles = town.createSection("roles");
            {
                ConfigurationSection owner = roles.createSection("Mayor");
                owner.set("name", "Mayor");
                owner.set("plural", "Mayors");
                owner.set("privileges", stringList(Privileges.all()));
            }
            {
                ConfigurationSection member = roles.createSection("Resident");
                member.set("name", "Resident");
                member.set("plural", "Residents");
                member.set("privileges", Collections.emptyList());
            }

            town.set("initial", "Resident");
            town.set("founder", "Mayor");
            town.set("friendly-fire", false);
            town.set("has-immediate-members", false);
            town.set("can-own-land", true);
            town.set("allowed-multiple", false);
            town.set("can-war", true);
            town.set("may-be-peaceful", true);
            town.set("can-tax", true);
        }

        return UniverseRules.load("test", config);
    }

    private static List<String> stringList(Collection<Privilege> privileges) {
        List<String> result = new ArrayList<>();
        for (Privilege privilege : privileges) {
            result.add(privilege.getName());
        }
        return result;
    }

    private PoliticsTaxTestObjectFactory() {
        throw new UnsupportedOperationException();
    }
}
