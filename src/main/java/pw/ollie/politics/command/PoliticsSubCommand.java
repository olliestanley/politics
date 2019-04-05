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
package pw.ollie.politics.command;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.args.Arguments;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic subcommand of any command in Politics.
 */
public abstract class PoliticsSubCommand {
    private final String name;

    protected PoliticsSubCommand(String name) {
        this.name = name.toLowerCase();
    }

    public abstract void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException;

    public String getName() {
        return name;
    }

    public abstract String getPermission();

    /**
     * Gets list of aliases. If there are none this should return an empty list, *not* {@code null}.
     * <p>
     * All entries in this list should be *lower case*.
     *
     * @return string list of aliases
     */
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    public abstract String getUsage();

    public abstract String getDescription();

    /**
     * Whether command is player-only. Should be overridden by subcommands which are.
     *
     * @return true if command is only for players, else false
     */
    public boolean isPlayerOnly() {
        return false;
    }
}
