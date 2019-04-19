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
package pw.ollie.politics.command.subplot;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.args.Arguments;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

// starts process create a new subplot in current plot
// need an area selection method for this
public class SubplotCreateCommand extends SubplotSubCommand {
    SubplotCreateCommand() {
        super("create");
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        // todo
    }

    @Override
    public String getPermission() {
        return "politics.plot.subplot.create";
    }

    @Override
    public String getUsage() {
        return "/subplot create";
    }

    @Override
    public String getDescription() {
        return "Adds a subplot to your current plot.";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("new", "add");
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
