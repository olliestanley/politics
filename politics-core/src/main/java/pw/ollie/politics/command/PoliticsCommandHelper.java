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

import pw.ollie.politics.util.StringUtil;
import pw.ollie.politics.util.collect.PagedList;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.util.message.MessageUtil;
import pw.ollie.politics.util.stream.CollectorUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class PoliticsCommandHelper {
    // todo docs
    public static final String GROUPS_ADMIN_PERMISSION = "politics.group.admin";
    public static final String PLOTS_ADMIN_PERMISSION = "politics.plot.admin";

    private static final int fuzzyTolerance = 2;

    public static void sendCommandHelp(CommandSender sender, PoliticsBaseCommand baseCommand) {
        PoliticsCommandHelper.sendCommandHelp(sender, baseCommand, 1);
    }

    // page counts from 1 (not an index)
    public static void sendCommandHelp(CommandSender sender, PoliticsBaseCommand baseCommand, int pageNumber) {
        PagedList<PoliticsSubcommand> pages = baseCommand.streamSubcommands()
                .filter(cmd -> can(sender, cmd.getPermission())).collect(CollectorUtil.toPagedList());
        if (pageNumber > pages.pages()) {
            MessageBuilder.beginError().append("There are only " + pages.pages() + " pages.").send(sender);
            return;
        }

        MessageBuilder message = MessageUtil.startBlockMessage("/" + baseCommand.getName() + " Help (" + pageNumber + " of " + pages.pages() + ")");
        List<PoliticsSubcommand> page = pages.getPage(pageNumber);
        page.forEach(sub -> message.newLine().highlight("/" + baseCommand.getName() + " " + sub.getName()).normal(" - " + sub.getDescription()));
        message.send(sender);
    }

    public static boolean hasPlotsAdmin(CommandSender source) {
        return source instanceof ConsoleCommandSender || source.hasPermission(PLOTS_ADMIN_PERMISSION);
    }

    public static boolean hasGroupsAdmin(CommandSender source) {
        return source instanceof ConsoleCommandSender || source.hasPermission(GROUPS_ADMIN_PERMISSION);
    }

    public static void registerPermission(String node) {
        PoliticsCommandHelper.registerPermission(node, "");
    }

    public static void registerPermission(String node, String description) {
        try {
            Permission permission = new Permission(node, description);
            Bukkit.getPluginManager().addPermission(permission);
        } catch (Exception ignore) {
            // ignore, this just means the permission is already registered
        }
    }

    public static Optional<PoliticsSubcommand> getClosestMatch(Collection<PoliticsSubcommand> subcommands, String label) {
        return Optional.ofNullable(fuzzyLookup(subcommands, label));
    }

    private static PoliticsSubcommand fuzzyLookup(Collection<PoliticsSubcommand> collection, String name) {
        String adjName = name.replaceAll("[ _]", "").toLowerCase();

        Optional<PoliticsSubcommand> result = collection.stream()
                .filter(cmd -> cmd.getName().equals(adjName) || cmd.getAliases().contains(adjName)).findAny();
        if (result.isPresent()) {
            return result.get();
        }

        char char0 = adjName.charAt(0);
        int lowest = -1;
        PoliticsSubcommand best = null;
        for (PoliticsSubcommand cmd : collection) {
            if (cmd.getName().charAt(0) != char0 && cmd.getAliases().stream().noneMatch(alias -> alias.charAt(0) == char0)) {
                continue;
            }

            int dist = StringUtil.getLevenshteinDistance(cmd.getName(), adjName);
            if (dist < fuzzyTolerance && (dist < lowest || lowest == -1)) {
                lowest = dist;
                best = cmd;
            } else {
                for (String alias : cmd.getAliases()) {
                    dist = StringUtil.getLevenshteinDistance(alias, adjName);
                    if (dist < fuzzyTolerance && (dist < lowest || lowest == -1)) {
                        lowest = dist;
                        best = cmd;
                    }
                }
            }
        }

        return best;
    }

    private static boolean can(CommandSender sender, String perm) {
        return perm == null || sender.hasPermission(perm);
    }

    private PoliticsCommandHelper() {
        throw new UnsupportedOperationException();
    }
}
