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
package pw.ollie.politics.command.politics;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.PoliticsSubcommand;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.util.message.MessageUtil;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class PoliticsHelpCommand extends PoliticsSubcommand {
    PoliticsHelpCommand() {
        super("help");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) {
        MessageBuilder message = MessageUtil.startBlockMessage("Politics - Command Overview");
        message.newLine().append("Universe Commands: ").highlight("/universe help");
        message.newLine().append("Plot Commands: ").highlight("/plot help");
        message.newLine().append("Subplot Commands: ").highlight("/subplot help");
        plugin.getUniverseManager().streamGroupLevels().forEach(level -> message.newLine().normal(level.getName())
                .append(" Commands: ").highlight("/").append(level.getId()).append(" help"));
        message.send(sender);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission() {
        return "politics.help";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAliases() {
        return Arrays.asList("commands", "cmds", "h");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage() {
        return "/politics help";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Provides general command help for Politics.";
    }
}
