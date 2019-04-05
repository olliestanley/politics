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

import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.Politics;
import pw.ollie.politics.command.args.Arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

public abstract class PoliticsBaseCommand extends BukkitCommand {
    private final Set<PoliticsSubCommand> subCommands = new THashSet<>();

    protected PoliticsBaseCommand(String name, String description) {
        super(name.toLowerCase(), description, "Type '/" + name + " help' for usage help!", new ArrayList<>());
    }

    public void runCommand(CommandSender sender, Arguments args) {
        // this is a default implementation - can be overridden in subclasses
        // if there is no override then the base command implementation simply searches for subcommands
        // if there is no valid subcommand provided then a hopefully helpful error message is shown

        if (args.length() < 1) {
            // todo helpful error message
            return;
        }

        String arg1 = args.getString(0);
        Optional<PoliticsSubCommand> subCommand = findSubCommand(arg1);

        if (subCommand.isPresent()) {
            if (checkPerms(subCommand.get(), sender)) {
                subCommand.get().runCommand(sender, args.subArgs(1, args.length() - 1));
            }
        } else {
            Optional<PoliticsSubCommand> closestMatch = PoliticsCommandHelper.getClosestMatch(subCommands, arg1);

            if (closestMatch.isPresent()) {
                sender.sendMessage("Unrecognised subcommand - did you mean '" + closestMatch.get().getName() + "'?");
                return;
            }

            // todo helpful error message
        }
    }

    protected boolean registerSubCommand(PoliticsSubCommand subCommand) {
        String subCommandName = subCommand.getName().toLowerCase();
        if (subCommands.stream()
                .anyMatch(registered -> registered.getName().equals(subCommandName) || registered.getAliases().contains(subCommandName))) {
            Politics.getLogger().log(Level.WARNING, "Duplicate command name or name used as alias for other command: " + subCommandName);
            return false;
        }

        String subCommandPerm = subCommand.getPermission();
        if (subCommandPerm != null) {
            PoliticsCommandHelper.registerPermission(subCommandPerm);
        }

        subCommands.add(subCommand);
        return true;
    }

    private Optional<PoliticsSubCommand> findSubCommand(String arg) {
        String lcArg = arg.toLowerCase();

        return subCommands.stream()
                .filter(cmd -> cmd.getName().equals(lcArg) || cmd.getAliases().contains(lcArg))
                .findFirst();
    }

    @Override
    public final boolean execute(CommandSender sender, String label, String[] args) {
        if (checkPerms(sender)) {
            this.runCommand(sender, new Arguments(args));
        }

        return true;
    }

    private boolean checkPerms(PoliticsSubCommand subCommand, CommandSender sender) {
        if (subCommand.getPermission() != null && !(sender.hasPermission(subCommand.getPermission()))) {
            sender.sendMessage("You don't have permission for that.");
            return false;
        }

        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage("You must be a player to do that.");
            return false;
        }

        return true;
    }

    private boolean checkPerms(CommandSender sender) {
        if (getPermission() != null && !(sender.hasPermission(getPermission()))) {
            sender.sendMessage("You don't have permission for that.");
            return false;
        }

        if (isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage("You must be a player to do that.");
            return false;
        }

        return true;
    }

    public String getPermission() {
        return null;
    }

    public boolean isPlayerOnly() {
        return false;
    }
}
