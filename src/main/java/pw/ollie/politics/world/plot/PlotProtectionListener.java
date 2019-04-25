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
import pw.ollie.politics.util.Pair;
import pw.ollie.politics.util.message.MessageUtil;
import pw.ollie.politics.world.WorldManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
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

/**
 * Applies {@link Plot} and {@link Subplot} region protections where applicable.
 */
public final class PlotProtectionListener implements Listener {
    private final PoliticsPlugin plugin;
    private final WorldManager worldManager;

    public PlotProtectionListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
        this.worldManager = plugin.getWorldManager();
    }

    // logic methods

    private void checkPlayerProtection(Player player, Block block, Cancellable event, PlotProtectionType type) {
        Location location = block.getLocation();
        Privilege privilege = type.getPermission();

        if (privilege == null) {
            event.setCancelled(true);
            MessageUtil.error(player, "You can't do that in this plot.");
            return;
        }

        PlotDamageSource source = new PlotDamageSource(player);

        Pair<Subplot, Boolean> subplotCheck = canInSubplot(player, location, privilege);
        if (!subplotCheck.getSecond()) {
            SubplotProtectionTriggerEvent triggerEvent = callSubplotProtectionEvent(subplotCheck.getFirst(), block, source, type);
            if (!triggerEvent.isCancelled()) {
                event.setCancelled(true);
                MessageUtil.error(player, "You can't do that in this plot.");
            }

            return;
        }

        Pair<Plot, Boolean> plotCheck = canInPlot(player, location, privilege);
        if (!plotCheck.getSecond()) {
            PlotProtectionTriggerEvent triggerEvent = callPlotProtectionEvent(plotCheck.getFirst(), block, source, type);
            if (!triggerEvent.isCancelled()) {
                event.setCancelled(true);
                MessageUtil.error(player, "You can't do that in this plot.");
            }
        }
    }

    private Pair<Plot, Boolean> canInPlot(Player player, Location location, Privilege privilege) {
        if (!(privilege.getTypes().contains(PrivilegeType.PLOT))) {
            throw new IllegalArgumentException("Must be a plot-type privilege");
        }

        Plot plot = worldManager.getPlotAt(location);
        return new Pair<>(plot, plot.can(player, privilege));
    }

    private Pair<Subplot, Boolean> canInSubplot(Player player, Location location, Privilege privilege) {
        if (!(privilege.getTypes().contains(PrivilegeType.PLOT))) {
            throw new IllegalArgumentException("Must be a plot-type privilege");
        }

        Plot plot = worldManager.getPlotAt(location);
        Subplot subplot = plot.getSubplotAt(location);
        if (subplot == null) {
            return new Pair<>(null, true);
        }
        return new Pair<>(subplot, subplot.can(player, privilege));
    }

    // listener methods which mostly simply make appropriate calls to above logic methods

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        checkPlayerProtection(event.getPlayer(), event.getBlock(), event, PlotProtectionType.BLOCK_BREAK);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        checkPlayerProtection(event.getPlayer(), event.getBlock(), event, PlotProtectionType.BLOCK_PLACE);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        for (BlockState block : event.getReplacedBlockStates()) {
            checkPlayerProtection(event.getPlayer(), block.getBlock(), event, PlotProtectionType.BLOCK_PLACE);

            if (event.isCancelled()) {
                break;
            }
        }
    }

    // todo the below two methods might not be great on performance - need to look into whether this can be improved
    // possibly some form of caching whether a certain action is acceptable

    // todo call relevant events in the below two methods
    // todo abstract some of the code in the below two methods

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        Block pistonBlock = event.getBlock();
        Location pistonLoc = pistonBlock.getLocation();

        Plot pistonPlot = worldManager.getPlotAt(pistonLoc);
        Subplot pistonSubplot = pistonPlot.getSubplotAt(pistonLoc);

        // prevent pulling blocks out of plots
        for (Block moved : event.getBlocks()) {
            Location movedLoc = moved.getLocation();

            Plot blockPlot = worldManager.getPlotAt(movedLoc);
            if (!blockPlot.hasOwner()) {
                continue;
            }

            if (blockPlot.getOwnerId() != pistonPlot.getOwnerId()) {
                // a block in an owned plot is being moved by a piston in a plot which either does not have an owner or
                // is owned by a different group to the owner of the plot the moved block is within - get rid
                event.setCancelled(true);
                break;
            }

            // both piston and invaded blocks are in the same plot - time to check subplots

            Subplot blockSubplot = blockPlot.getSubplotAt(movedLoc);
            if (blockSubplot == null) {
                continue;
            }

            if (pistonSubplot == null || !pistonSubplot.getOwnerId().equals(blockSubplot.getOwnerId())) {
                // a block in an owned subplot is being moved by a piston in a subplot which either does not have an
                // owner or is owned by someone other than the other of the plot the moved block is within - cancel
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Block pistonBlock = event.getBlock();
        Location pistonLoc = pistonBlock.getLocation();

        List<Block> blocks = event.getBlocks();

        Plot pistonPlot = worldManager.getPlotAt(pistonLoc);
        Subplot pistonSubplot = pistonPlot.getSubplotAt(pistonLoc);

        // no blocks moving, check if push into (sub)plot from outside to prevent breaking flimsy blocks (e.g torches)
        if (blocks.isEmpty()) {
            Block invaded = pistonBlock.getRelative(event.getDirection());
            if (invaded.getType() == Material.AIR) {
                // doesn't matter as it isn't going to do anything
                return;
            }

            Location invadedLoc = invaded.getLocation();

            Plot invadedPlot = worldManager.getPlotAt(invadedLoc);
            if (!invadedPlot.hasOwner()) {
                return;
            }

            if (pistonPlot.getOwnerId() != invadedPlot.getOwnerId()) {
                // invaded block plot has an owner and piston plot either doesn't or has a different owner - rude
                event.setCancelled(true);
                return;
            }

            // both piston and invaded blocks are in the same plot - time to check subplots

            Subplot invadedSubplot = invadedPlot.getSubplotAt(invadedLoc);
            if (invadedSubplot == null) {
                return;
            }

            if (pistonSubplot == null || !pistonSubplot.getOwnerId().equals(invadedSubplot.getOwnerId())) {
                // pushing into someone else's subplot - nope
                event.setCancelled(true);
                return;
            }

            return;
        }

        // check if any pushed block is in a plot owned by a different group to the piston owner
        // also prevent blocks being pushed into a plot from outside
        for (Block moved : blocks) {
            Location movedLoc = moved.getLocation();

            // first check for a block in one plot being pushed into another plot
            Location destination = moved.getRelative(event.getDirection()).getLocation();
            Plot destinationPlot = worldManager.getPlotAt(destination);
            if (destinationPlot.hasOwner() && destinationPlot.getOwnerId() != pistonPlot.getOwnerId()) {
                event.setCancelled(true);
                break;
            }

            Subplot destinationSubplot = destinationPlot.getSubplotAt(destination);
            if (destinationSubplot != null && (pistonSubplot == null || !destinationSubplot.getOwnerId().equals(pistonSubplot.getOwnerId()))) {
                event.setCancelled(true);
                break;
            }

            Plot blockPlot = worldManager.getPlotAt(movedLoc);
            if (!blockPlot.hasOwner()) {
                continue;
            }

            if (blockPlot.getOwnerId() != pistonPlot.getOwnerId()) {
                // a block in an owned plot is being moved by a piston in a plot which either does not have an owner or
                // is owned by a different group to the owner of the plot the moved block is within - get rid
                event.setCancelled(true);
                break;
            }

            // both piston and pushed blocks are in the same plot - time to check subplots

            Subplot blockSubplot = blockPlot.getSubplotAt(movedLoc);
            if (blockSubplot == null) {
                continue;
            }

            if (pistonSubplot == null || !pistonSubplot.getOwnerId().equals(blockSubplot.getOwnerId())) {
                // a block in an owned subplot is being moved by a piston in a subplot which either does not have an
                // owner or is owned by someone other than the other of the plot the moved block is within - cancel
                event.setCancelled(true);
                break;
            }
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockDispense(BlockDispenseEvent event) {
        // stop dispensers being used to dispense blocks (e.g. lava) or item between (sub)plots
        // todo cancel if from one plot to another or from outside plot into plot
    }

    private PlotProtectionTriggerEvent callPlotProtectionEvent(Plot plot, Block damaged, PlotDamageSource source, PlotProtectionType type) {
        return PoliticsEventFactory.callPlotProtectionTriggerEvent(plot, damaged, source, type);
    }

    private SubplotProtectionTriggerEvent callSubplotProtectionEvent(Subplot subplot, Block damaged, PlotDamageSource source, PlotProtectionType type) {
        return PoliticsEventFactory.callSubplotProtectionTriggerEvent(subplot.getParent(), subplot, damaged, source, type);
    }
}
