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
import pw.ollie.politics.universe.RuleTemplates;
import pw.ollie.politics.universe.UniverseRules;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class UniverseGenRulesCommand extends PoliticsSubCommand {
    UniverseGenRulesCommand() {
        super("genrules");
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) {
        if (args.length() < 2) {
            sender.sendMessage("Please specify the template name and the name of the new rules.");
            return;
        }

        String templateName = args.getString(0).toLowerCase();
        Set<String> templateNames = RuleTemplates.listTemplateNames();
        if (!templateNames.contains(templateName)) {
            sender.sendMessage("A template with the name of '" + templateName + "' does not exist.");
            return;
        }

        String name = args.getString(1).toLowerCase();
        UniverseRules existing = plugin.getUniverseManager().getRules(name);

        boolean force = args.hasNonValueFlag("f");
        if (existing != null && !force) {
            sender.sendMessage("A set of rules with the name of '" + name
                    + "' already exists. Use the '--f' option to overwrite an existing rule set.");
            return;
        }

        RuleTemplates.copyTemplate(templateName, name);
        sender.sendMessage("A new set of rules named '" + name + "' based on the template '" + templateName
                + "' has been generated. Please restart the server to see your changes.");
    }

    @Override
    public String getPermission() {
        return "politics.universe.genrules";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("makerules", "createrules");
    }

    @Override
    public String getUsage() {
        return "/universe genrules <template> <name> [--f]";
    }

    @Override
    public String getDescription() {
        return "Generates a new set of rules from a template.";
    }
}
