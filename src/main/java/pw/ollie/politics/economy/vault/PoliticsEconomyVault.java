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
package pw.ollie.politics.economy.vault;

import net.milkbowl.vault.economy.Economy;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.economy.PoliticsEconomy;
import pw.ollie.politics.economy.PoliticsEconomyResult;
import pw.ollie.politics.group.Group;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

/**
 * Vault-based economy functions implementation for Politics.
 */
public class PoliticsEconomyVault extends PoliticsEconomy {
    private Economy vaultEconomy;

    public PoliticsEconomyVault(PoliticsPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean loadEconomy() {
        Server server = getPlugin().getServer();
        if (server.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        vaultEconomy = rsp.getProvider();
        return vaultEconomy != null;
    }

    @Override
    public PoliticsEconomyResult taxMember(Group group, UUID member, double amount) {
        return null;
    }
}
