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
import pw.ollie.politics.activity.ActivityManager;
import pw.ollie.politics.activity.PoliticsActivity;
import pw.ollie.politics.command.CommandException;
import pw.ollie.politics.command.PoliticsSubCommand;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PoliticsCancelCommand extends PoliticsSubCommand {
    PoliticsCancelCommand() {
        super("cancel");
    }

    @Override
    public void runCommand(PoliticsPlugin plugin, CommandSender sender, Arguments args) throws CommandException {
        Player player = (Player) sender;
        ActivityManager activityManager = plugin.getActivityManager();
        PoliticsActivity activity = activityManager.getActivity(player);
        if (activity == null) {
            throw new CommandException("You do not have an ongoing activity to cancel.");
        }
        activity.complete();
        plugin.getActivityManager().endActivity(player);
        MessageBuilder.begin("Your ").highlight(activity.getName()).normal(" was cancelled.").send(sender);
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getUsage() {
        return "/politics cancel";
    }

    @Override
    public String getDescription() {
        return "Cancels an ongoing activity";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
