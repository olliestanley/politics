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
package pw.ollie.politics.command.universe;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.PoliticsSubCommand;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.universe.UniverseRules;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class UniverseRulesCommand extends PoliticsSubCommand {
    UniverseRulesCommand() {
        super("rules");
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) {
        List<UniverseRules> ruleList = plugin.getUniverseManager().listRules();
        for (UniverseRules rules : ruleList) {
            sender.sendMessage(rules.getName() + " - " + rules.getDescription());
        }
    }

    @Override
    public String getPermission() {
        return "politics.universe.rules";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("r, rulesets");
    }

    @Override
    public String getUsage() {
        return "/universe rules";
    }
}
