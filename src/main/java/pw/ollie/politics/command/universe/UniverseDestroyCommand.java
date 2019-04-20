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
import pw.ollie.politics.command.PoliticsSubCommand;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class UniverseDestroyCommand extends PoliticsSubCommand {
    UniverseDestroyCommand() {
        super("destroy");
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        if (args.length(false) < 1) {
            throw new CommandException("Please specify the name of the universe to destroy.");
        }

        Universe universe = plugin.getUniverseManager().getUniverse(args.getString(0, false));
        if (universe == null) {
            throw new CommandException("A universe with the name '" + args.getString(0, false) + "' doesn't exist.");
        }

        plugin.getUniverseManager().destroyUniverse(universe);
        PoliticsEventFactory.callUniverseDestroyEvent(universe);
        MessageBuilder.begin("Universe destroyed, sir.").send(sender);
    }

    @Override
    public String getPermission() {
        return "politics.universe.destroy";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("delete", "remove");
    }

    @Override
    public String getUsage() {
        return "/universe destroy <universe>";
    }

    @Override
    public String getDescription() {
        return "Destroys a universe.";
    }
}
