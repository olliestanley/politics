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

import gnu.trove.map.hash.THashMap;

import pw.ollie.politics.util.StringUtil;
import pw.ollie.politics.util.collect.PagedArrayList;
import pw.ollie.politics.util.collect.PagedList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PoliticsCommandHelper {
    private static Map<PoliticsBaseCommand, PagedList<PoliticsSubCommand>> pagedSubCommands = new THashMap<>();

    public static void sendCommandHelp(CommandSender sender, PoliticsBaseCommand baseCommand) {
        PoliticsCommandHelper.sendCommandHelp(sender, baseCommand, 1);
    }

    // page counts from 1 (not an index)
    public static void sendCommandHelp(CommandSender sender, PoliticsBaseCommand baseCommand, int pageNumber) {
        PagedList<PoliticsSubCommand> pages = pagedSubCommands.computeIfAbsent(baseCommand, f -> generatePagedSubCommands(baseCommand));
        if (pageNumber > pages.pages()) {
            sender.sendMessage("There are only " + pages.pages() + " pages.");
            return;
        }

        List<PoliticsSubCommand> page = pages.getPage(pageNumber);
        // todo provide helpful message with subcommands
    }

    public static Optional<PoliticsSubCommand> getClosestMatch(Collection<PoliticsSubCommand> subCommands, String label) {
        return fuzzyLookup(subCommands, label, 2);
    }

    private static PagedList<PoliticsSubCommand> generatePagedSubCommands(PoliticsBaseCommand baseCommand) {
        return new PagedArrayList<>(baseCommand.getSubCommands());
    }

    private static Optional<PoliticsSubCommand> fuzzyLookup(Collection<PoliticsSubCommand> collection, String name, int tolerance) {
        String adjName = name.replaceAll("[ _]", "").toLowerCase();

        Optional<PoliticsSubCommand> result = collection.stream()
                .filter(cmd -> cmd.getName().toLowerCase().equals(adjName) || cmd.getAliases().contains(name))
                .findAny();
        if (result.isPresent()) {
            return result;
        }

        int lowest = -1;
        PoliticsSubCommand best = null;
        for (PoliticsSubCommand cmd : collection) {
            char char0 = adjName.charAt(0);
            if (cmd.getName().charAt(0) != char0 && cmd.getAliases().stream().noneMatch(alias -> alias.charAt(0) == char0)) {
                continue;
            }

            int dist = StringUtil.getLevenshteinDistance(cmd.getName(), adjName);
            if (dist < tolerance && (dist < lowest || lowest == -1)) {
                lowest = dist;
                best = cmd;
            } else {
                for (String alias : cmd.getAliases()) {
                    dist = StringUtil.getLevenshteinDistance(alias, adjName);
                    if (dist < tolerance && (dist < lowest || lowest == -1)) {
                        lowest = dist;
                        best = cmd;
                    }
                }
            }
        }

        if (best == null) {
            return Optional.empty();
        } else {
            return Optional.of(best);
        }
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

    private PoliticsCommandHelper() {
        throw new UnsupportedOperationException();
    }
}
