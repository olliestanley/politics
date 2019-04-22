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
package pw.ollie.politics.world.plot;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.plot.PlotProtectionTriggerEvent;
import pw.ollie.politics.event.plot.subplot.SubplotProtectionTriggerEvent;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.group.privilege.PrivilegeType;
import pw.ollie.politics.group.privilege.Privileges;
import pw.ollie.politics.util.Pair;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.List;

public final class PlotProtectionListener implements Listener {
    private final PoliticsPlugin plugin;

    public PlotProtectionListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    // todo update this class to use the new messaging system

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        // todo abstract the below code so it isn't duplicated over and over

        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        Pair<Subplot, Boolean> subplotCheck = canInSubplot(player, location, Privileges.GroupPlot.BUILD);
        if (!subplotCheck.getSecond()) {
            SubplotProtectionTriggerEvent triggerEvent = callSubplotProtectionEvent(subplotCheck.getFirst(), event.getBlock(),
                    new PlotDamageSource(player.getUniqueId()), PlotProtectionTriggerEvent.PlotProtectionType.BLOCK_BREAK);
            if (!triggerEvent.isCancelled()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You can't build in this plot.");
            }

            return;
        }

        Pair<Plot, Boolean> plotCheck = canInPlot(player, location, Privileges.GroupPlot.BUILD);
        if (!plotCheck.getSecond()) {
            PlotProtectionTriggerEvent triggerEvent = callPlotProtectionEvent(plotCheck.getFirst(), event.getBlock(),
                    new PlotDamageSource(player.getUniqueId()), PlotProtectionTriggerEvent.PlotProtectionType.BLOCK_BREAK);
            if (!triggerEvent.isCancelled()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You can't build in this subplot.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        // todo
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        for (BlockState block : event.getReplacedBlockStates()) {
            // todo
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        // prevent pulling blocks out of plots
        for (Block moved : event.getBlocks()) {
            // todo check plot of moved block, compare to plot of piston
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Block pistonBlock = event.getBlock();
        List<Block> blocks = event.getBlocks();

        // if no blocks are moving, check if it's pushing into a plot from outside
        // prevents pistons breaking 'flimsy' blocks in plots (e.g torches)
        if (blocks.size() == 0) {
            Block invadedBlock = pistonBlock.getRelative(event.getDirection());

            if (invadedBlock.getType() == Material.AIR) {
                return;
            }

            // todo compare the plot at the pistonBlock location and the invadedBlock location
            return;
        }

        // check if any pushed block is in a plot owned by a different group to the piston owner
        // also prevent blocks being pushed into a plot from outside
        for (int i = 0; i < blocks.size(); i++) {
            // todo compare plot of block to pistomBlock
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockForm(BlockFormEvent event) {
        // todo check if in a plot
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStructureGrow(StructureGrowEvent event) {
        // todo make configurable whether to prevent this
        for (BlockState block : event.getBlocks()) {
            // todo prevent trees outside plots growing into a plot
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            // todo check if this is allowed
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBurn(BlockBurnEvent event) {
        // todo check plot of burning block
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockFromTo(BlockFromToEvent event) {
        // todo prevent blocks flowing from one plot to another
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockSpread(BlockSpreadEvent event) {
        // todo make configurable whether this is enabled
        if (event.getSource().getType() != Material.FIRE) {
            return;
        }

        // todo check if in a plot
    }

    //ensures dispensers can't be used to dispense a block(like water or lava) or item across a claim boundary
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockDispense(BlockDispenseEvent event) {
        // todo cancel if from one plot to another or from outside plot into plot
    }

    private PlotProtectionTriggerEvent callPlotProtectionEvent(Plot plot, Block damaged, PlotDamageSource source, PlotProtectionTriggerEvent.PlotProtectionType type) {
        return PoliticsEventFactory.callPlotProtectionTriggerEvent(plot, damaged, source, type);
    }

    private SubplotProtectionTriggerEvent callSubplotProtectionEvent(Subplot subplot, Block damaged, PlotDamageSource source, PlotProtectionTriggerEvent.PlotProtectionType type) {
        return PoliticsEventFactory.callSubplotProtectionTriggerEvent(subplot.getParent(), subplot, damaged, source, type);
    }

    private Pair<Plot, Boolean> canInPlot(Player player, Location location, Privilege privilege) {
        if (!(privilege.getTypes().contains(PrivilegeType.PLOT))) {
            throw new IllegalArgumentException("Must be a plot-type privilege");
        }

        Plot plot = plugin.getWorldManager().getPlotAt(location);
        return new Pair<>(plot, plot.can(player, privilege));
    }

    private Pair<Subplot, Boolean> canInSubplot(Player player, Location location, Privilege privilege) {
        if (!(privilege.getTypes().contains(PrivilegeType.PLOT))) {
            throw new IllegalArgumentException("Must be a plot-type privilege");
        }

        Plot plot = plugin.getWorldManager().getPlotAt(location);
        Subplot subplot = plot.getSubplotAt(location);
        if (subplot == null) {
            return new Pair<>(null, true);
        }
        return new Pair<>(subplot, subplot.can(player, privilege));
    }
}
