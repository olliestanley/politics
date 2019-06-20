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
import pw.ollie.politics.util.collect.CollectionUtil;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PoliticsBaseCommand extends BukkitCommand {
    // todo docs
    private final PoliticsPlugin plugin;
    private final List<PoliticsSubcommand> subcommands;

    protected PoliticsBaseCommand(PoliticsPlugin plugin, String name, String description) {
        super(name.toLowerCase(), description, "Type '/" + name + " help' for usage help!", new ArrayList<>());
        this.plugin = plugin;
        this.subcommands = new ArrayList<>();
    }

    // streamSubcommands, getPermission, isPlayerOnly, runCommand and registerSubcommand are default implementations which can be overridden

    public Stream<PoliticsSubcommand> streamSubcommands() {
        return subcommands.stream();
    }

    public String getPermission() {
        return null;
    }

    public boolean isPlayerOnly() {
        return false;
    }

    protected void runCommand(CommandSender sender, Arguments args) {
        // this is a default implementation - can be overridden in subclasses
        // if there is no override then the base command implementation simply searches for subcommands
        // if there is no valid subcommand provided then a hopefully helpful error message is shown

        if (args.length() < 1) {
            PoliticsCommandHelper.sendCommandHelp(sender, this);
            return;
        }

        String arg1 = args.getString(0);
        PoliticsSubcommand subcommand = findSubCommand(arg1);

        if (subcommand != null && checkPerms(subcommand, sender)) {
            try {
                subcommand.runCommand(plugin, sender, args.subArgs(1, args.length(false)));
            } catch (CommandException e) {
                MessageBuilder.beginError().append(e.getMessage()).send(sender);
            }
        } else {
            Optional<PoliticsSubcommand> closestMatch = PoliticsCommandHelper.getClosestMatch(subcommands, arg1);

            if (closestMatch.isPresent()) {
                MessageBuilder.beginError().append("Unrecognised subcommand - did you mean '")
                        .append(closestMatch.get().getName()).append("'?").send(sender);
                return;
            }

            PoliticsCommandHelper.sendCommandHelp(sender, this);
        }
    }

    public boolean registerSubCommand(PoliticsSubcommand subcommand) {
        String subcommandName = subcommand.getName().toLowerCase();
        if (subcommands.stream()
                .anyMatch(registered -> registered.getName().equals(subcommandName) || registered.getAliases().contains(subcommandName))) {
            Politics.getLogger().log(Level.WARNING, "Duplicate command name or name used as alias for other command: " + subcommandName);
            return false;
        }

        String subcommandPerm = subcommand.getPermission();
        if (subcommandPerm != null) {
            PoliticsCommandHelper.registerPermission(subcommandPerm);
        }

        subcommands.add(subcommand);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean execute(CommandSender sender, String label, String[] args) {
        if (checkPerms(sender)) {
            this.runCommand(sender, new Arguments(args));
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> tabComplete(CommandSender sender, String name, String[] args, Location location) {
        if (args.length < 2) {
            List<String> names = subcommands.stream().map(PoliticsSubcommand::getName).collect(Collectors.toList());
            return CollectionUtil.sorted(StringUtil.copyPartialMatches(args[0], names, new ArrayList<>()));
        }

        return Collections.emptyList();
    }

    private PoliticsSubcommand findSubCommand(String arg) {
        String lcArg = arg.toLowerCase();
        return subcommands.stream().filter(cmd -> cmd.getName().equals(lcArg) || cmd.getAliases().contains(lcArg))
                .findFirst().orElse(null);
    }

    private boolean checkPerms(PoliticsSubcommand subcommand, CommandSender sender) {
        if (subcommand.getPermission() != null && !(sender.hasPermission(subcommand.getPermission()))) {
            MessageBuilder.beginError().append("You don't have permission for that.").send(sender);
            return false;
        }

        if (subcommand.isPlayerOnly() && !(sender instanceof Player)) {
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
}
