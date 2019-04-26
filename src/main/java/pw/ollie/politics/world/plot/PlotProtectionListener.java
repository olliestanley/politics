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
import pw.ollie.politics.world.WorldConfig;
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
import org.bukkit.material.Dispenser;

import java.util.List;

/**
 * Applies {@link Plot} and {@link Subplot} region protections where applicable.
 */
public final class PlotProtectionListener implements Listener {
    private final WorldManager worldManager;

    public PlotProtectionListener(PoliticsPlugin plugin) {
        this.worldManager = plugin.getWorldManager();
    }

    // logic methods

    private void checkPlayerProtection(Player player, Block block, Cancellable event, PlotProtectionType type) {
        WorldConfig worldConfig = worldManager.getWorld(block.getWorld()).getConfig();
        if (!worldConfig.hasPlots()) {
            return;
        }

        Location location = block.getLocation();
        Privilege privilege = type.getPermission();

        if (privilege == null) {
            event.setCancelled(true);
            MessageUtil.error(player, "You can't do that in this plot.");
            return;
        }

        PlotDamageSource source = new PlotDamageSource(player);

        if (worldConfig.hasSubplots()) {
            Pair<Subplot, Boolean> subplotCheck = canInSubplot(player, location, privilege);
            if (!subplotCheck.getSecond()) {
                SubplotProtectionTriggerEvent triggerEvent = callSubplotProtectionEvent(subplotCheck.getFirst(), block, source, type);
                if (!triggerEvent.isCancelled()) {
                    event.setCancelled(true);
                    MessageUtil.error(player, "You can't do that in this subplot.");
                    return;
                }
            }
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

    private void checkBlockProtection(Block sourceBlock, Plot sourcePlot, Subplot sourceSubplot, Block target, Cancellable event, PlotProtectionType type) {
        WorldConfig worldConfig = worldManager.getWorld(target.getWorld()).getConfig();
        if (!worldConfig.hasPlots()) {
            return;
        }

        Location targetLoc = target.getLocation();

        Plot blockPlot = worldManager.getPlotAt(targetLoc);
        if (!blockPlot.hasOwner()) {
            return;
        }

        if (blockPlot.getOwnerId() != sourcePlot.getOwnerId()) {
            // a block in an owned plot is being moved by a piston in a plot which either does not have an owner or
            // is owned by a different group to the owner of the plot the moved block is within - get rid
            PlotProtectionTriggerEvent protectEvent = PoliticsEventFactory.callPlotProtectionTriggerEvent(
                    blockPlot, target, new PlotDamageSource(sourceBlock), type);
            if (!protectEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }

        if (!worldConfig.hasSubplots()) {
            return;
        }

        // both piston and invaded blocks are in the same plot - time to check subplots

        Subplot blockSubplot = blockPlot.getSubplotAt(targetLoc);
        if (blockSubplot == null) {
            return;
        }

        if (sourceSubplot == null || !sourceSubplot.getOwnerId().equals(blockSubplot.getOwnerId())) {
            // a block in an owned subplot is being moved by a piston in a subplot which either does not have an
            // owner or is owned by someone other than the other of the plot the moved block is within - cancel
            SubplotProtectionTriggerEvent protectEvent = PoliticsEventFactory.callSubplotProtectionTriggerEvent(
                    blockPlot, blockSubplot, target, new PlotDamageSource(sourceBlock), type);
            if (!protectEvent.isCancelled()) {
                event.setCancelled(true);
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        Block pistonBlock = event.getBlock();
        Location pistonLoc = pistonBlock.getLocation();

        Plot pistonPlot = worldManager.getPlotAt(pistonLoc);
        Subplot pistonSubplot = pistonPlot.getSubplotAt(pistonLoc);

        // prevent pulling blocks out of plots
        for (Block moved : event.getBlocks()) {
            checkBlockProtection(pistonBlock, pistonPlot, pistonSubplot, moved, event, PlotProtectionType.PISTON_PULL);

            if (event.isCancelled()) {
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

            checkBlockProtection(pistonBlock, pistonPlot, pistonSubplot, invaded, event, PlotProtectionType.PISTON_PUSH);
            return;
        }

        // check if any pushed block is in a plot owned by a different group to the piston owner
        // also prevent blocks being pushed into a plot from outside
        for (Block moved : blocks) {
            // first check for a block in one plot being pushed into another plot
            checkBlockProtection(pistonBlock, pistonPlot, pistonSubplot, moved.getRelative(event.getDirection()), event, PlotProtectionType.PISTON_PUSH);
            if (event.isCancelled()) {
                break;
            }

            // then check for a block in another plot being pushed
            checkBlockProtection(pistonBlock, pistonPlot, pistonSubplot, moved, event, PlotProtectionType.PISTON_PUSH);
            if (event.isCancelled()) {
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockDispense(BlockDispenseEvent event) {
        // stop dispensers being used to dispense blocks (e.g. lava) or item between (sub)plots
        Block dispenserBlock = event.getBlock();
        Location dispenserLoc = dispenserBlock.getLocation();
        Dispenser dispenser = new Dispenser(Material.DISPENSER, dispenserBlock.getData());

        Plot sourcePlot = worldManager.getPlotAt(dispenserLoc);
        Subplot sourceSubplot = sourcePlot.getSubplotAt(dispenserLoc);

        Block targetBlock = dispenserBlock.getRelative(dispenser.getFacing());

        checkBlockProtection(dispenserBlock, sourcePlot, sourceSubplot, targetBlock, event, PlotProtectionType.DISPENSER);
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

        // todo also check if burning is allowed etc
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBurn(BlockBurnEvent event) {
        // todo check plot of burning block
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockFromTo(BlockFromToEvent event) {
        // protect from things like lava spreading into a plot or subplot from outside
        Block sourceBlock = event.getBlock();
        Location sourceLoc = sourceBlock.getLocation();

        Plot sourcePlot = worldManager.getPlotAt(sourceLoc);
        Subplot sourceSubplot = sourcePlot.getSubplotAt(sourceLoc);

        Block targetBlock = event.getToBlock();

        checkBlockProtection(sourceBlock, sourcePlot, sourceSubplot, targetBlock, event, PlotProtectionType.BLOCK_FLOW);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockSpread(BlockSpreadEvent event) {
        // todo make configurable whether this is enabled
        if (event.getSource().getType() != Material.FIRE) {
            return;
        }

        // todo check if in a plot
    }

    private PlotProtectionTriggerEvent callPlotProtectionEvent(Plot plot, Block damaged, PlotDamageSource source, PlotProtectionType type) {
        return PoliticsEventFactory.callPlotProtectionTriggerEvent(plot, damaged, source, type);
    }

    private SubplotProtectionTriggerEvent callSubplotProtectionEvent(Subplot subplot, Block damaged, PlotDamageSource source, PlotProtectionType type) {
        return PoliticsEventFactory.callSubplotProtectionTriggerEvent(subplot.getParent(), subplot, damaged, source, type);
    }
}
