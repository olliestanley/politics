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
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.PoliticsSubcommand;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.universe.UniverseRules;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.util.serial.ConfigUtil;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class UniverseGenRulesCommand extends PoliticsSubcommand {
    UniverseGenRulesCommand() {
        super("genrules");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        if (args.length() < 2) {
            throw new CommandException("Please specify the template name and the name of the new rules.");
        }

        String templateName = args.getString(0).toLowerCase();

        String name = args.getString(1).toLowerCase();
        UniverseRules existing = plugin.getUniverseManager().getRules(name);

        boolean force = args.hasNonValueFlag("f");
        if (existing != null && !force) {
            throw new CommandException("A set of rules with the name of '" + name
                    + "' already exists. Use the '--f' option to overwrite an existing rule set.");
        }

        if (ConfigUtil.copyUniverseRulesTemplate(templateName, name)) {
            MessageBuilder.begin("A new set of rules named '").highlight(name).normal("' based on the template '")
                    .highlight(templateName).normal("' has been generated. Please restart the server to see your changes.")
                    .send(sender);
        } else {
            throw new CommandException("The set of rules could not be created based on that template. Does the template exist?");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return "politics.universe.genrules";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAliases() {
        return Arrays.asList("makerules", "createrules");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/universe genrules <template> <name> [--f]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Generates a new set of rules from a template.";
    }
}
