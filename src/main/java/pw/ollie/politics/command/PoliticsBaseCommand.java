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

import pw.ollie.politics.Politics;
import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.command.args.Arguments;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class PoliticsBaseCommand extends BukkitCommand {
    private final PoliticsPlugin plugin;
    private final List<PoliticsSubCommand> subCommands = new ArrayList<>();

    protected PoliticsBaseCommand(PoliticsPlugin plugin, String name, String description) {
        super(name.toLowerCase(), description, "Type '/" + name + " help' for usage help!", new ArrayList<>());
        this.plugin = plugin;
    }

    public void runCommand(CommandSender sender, Arguments args) {
        // this is a default implementation - can be overridden in subclasses
        // if there is no override then the base command implementation simply searches for subcommands
        // if there is no valid subcommand provided then a hopefully helpful error message is shown

        if (args.length() < 1) {
            PoliticsCommandHelper.sendCommandHelp(sender, this);
            return;
        }

        String arg1 = args.getString(0);
        PoliticsSubCommand subCommand = findSubCommand(arg1);

        if (subCommand != null) {
            if (checkPerms(subCommand, sender)) {
                try {
                    subCommand.runCommand(plugin, sender, args.subArgs(1, args.length(false)));
                } catch (CommandException e) {
                    MessageBuilder.beginError().append(e.getMessage()).send(sender);
                }
            }
        } else {
            PoliticsSubCommand closestMatch = PoliticsCommandHelper.getClosestMatch(subCommands, arg1);

            if (closestMatch != null) {
                MessageBuilder.beginError().append("Unrecognised subcommand - did you mean '")
                        .append(closestMatch.getName()).append("'?").send(sender);
                return;
            }

            PoliticsCommandHelper.sendCommandHelp(sender, this);
        }
    }

    public boolean registerSubCommand(PoliticsSubCommand subCommand) {
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

    public List<PoliticsSubCommand> getSubCommands() {
        return new ArrayList<>(subCommands);
    }

    private PoliticsSubCommand findSubCommand(String arg) {
        String lcArg = arg.toLowerCase();

        return subCommands.stream()
                .filter(cmd -> cmd.getName().equals(lcArg) || cmd.getAliases().contains(lcArg))
                .findFirst().orElse(null);
    }

    @Override
    public final boolean execute(CommandSender sender, String label, String[] args) {
        if (checkPerms(sender)) {
            this.runCommand(sender, new Arguments(args));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String name, String[] args, Location location) {
        // todo check if this is the right check - only tab complete if they haven't typed beyond the first argument
        if (args.length < 2) {
            List<String> names = subCommands.stream().map(PoliticsSubCommand::getName).collect(Collectors.toList());
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], names, completions);
            Collections.sort(completions);
            return completions;
        }

        // todo completion for arguments beyond the subcommand?
        return new ArrayList<>();
    }

    private boolean checkPerms(PoliticsSubCommand subCommand, CommandSender sender) {
        if (subCommand.getPermission() != null && !(sender.hasPermission(subCommand.getPermission()))) {
            MessageBuilder.beginError().append("You don't have permission for that.").send(sender);
            return false;
        }

        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            MessageBuilder.beginError().append("You must be a player to do that.").send(sender);
            return false;
        }

        return true;
    }

    private boolean checkPerms(CommandSender sender) {
        if (getPermission() != null && !(sender.hasPermission(getPermission()))) {
            MessageBuilder.beginError().append("You don't have permission for that.").send(sender);
            return false;
        }

        if (isPlayerOnly() && !(sender instanceof Player)) {
            MessageBuilder.beginError().append("You must be a player to do that.").send(sender);
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
