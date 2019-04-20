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
package pw.ollie.politics.command.plot;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.args.Arguments;

import org.bukkit.command.CommandSender;

public class PlotViewCommand extends PlotSubCommand {
    PlotViewCommand() {
        super("view");
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        // todo
        // use fake blocks to show the outline of the plot the player is in
    }

    @Override
    public String getPermission() {
        return "politics.plot.view";
    }

    @Override
    public String getUsage() {
        return "/plot view";
    }

    @Override
    public String getDescription() {
        return "Visualise the outline of your current plot";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}