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
package net.oliverstanley.politics.listener;

import net.oliverstanley.politics.PoliticsPlugin;
import net.oliverstanley.politics.event.PoliticsEventFactory;
import net.oliverstanley.politics.event.plot.PlotProtectionTriggerEvent;
import net.oliverstanley.politics.event.plot.subplot.SubplotProtectionTriggerEvent;
import net.oliverstanley.politics.privilege.Privilege;
import net.oliverstanley.politics.privilege.PrivilegeType;
import net.oliverstanley.politics.world.WorldConfig;
import net.oliverstanley.politics.world.WorldHandler;
import net.oliverstanley.politics.world.plot.Plot;
import net.oliverstanley.politics.world.plot.PlotDamageSource;
import net.oliverstanley.politics.world.plot.PlotProtectionType;
import net.oliverstanley.politics.world.plot.ProtectedRegionCuboid;
import net.oliverstanley.politics.world.plot.Subplot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.Dispenser;

import java.util.List;

/**
 * Applies {@link Plot} and {@link Subplot} region protections where applicable.
 */
public final class ProtectionListener implements Listener {
    private final WorldHandler worldHandler;

    public ProtectionListener(PoliticsPlugin plugin) {
        this.worldHandler = plugin.getWorldHandler();
    }

    // logic methods

    private <T extends Event & Cancellable> void checkPlayerProtection(Player player, Block block, T event, PlotProtectionType type) {
        WorldConfig worldConfig = worldHandler.getWorld(block.getWorld()).getConfig();
        if (!worldConfig.hasPlots()) {
            return;
        }

        Location location = block.getLocation();
        Privilege privilege = type.getPermission();

        if (privilege == null) {
            event.setCancelled(true);
            // todo
//            MessageUtil.error(player, "You can't do that in this plot.");
            return;
        }

        PlotDamageSource source = new PlotDamageSource(player);

        if (worldConfig.hasSubplots()) {
            ProtectionCheck<Subplot> subplotCheck = checkSubplotPrivileges(player, location, privilege);
            if (!subplotCheck.getResult()) {
                SubplotProtectionTriggerEvent triggerEvent = PoliticsEventFactory.callSubplotProtectionTriggerEvent(
                        subplotCheck.getChecked().getParent(), subplotCheck.getChecked(), block, source, type, event);
                if (!triggerEvent.isCancelled()) {
                    event.setCancelled(true);
                    // todo
//                    MessageUtil.error(player, "You can't do that in this subplot.");
                    return;
                }
            }
        }

        ProtectionCheck<Plot> plotCheck = checkPlotPrivileges(player, location, privilege);
        if (!plotCheck.getResult()) {
            PlotProtectionTriggerEvent triggerEvent = PoliticsEventFactory.callPlotProtectionTriggerEvent(
                    plotCheck.getChecked(), block, source, type, event);
            if (!triggerEvent.isCancelled()) {
                event.setCancelled(true);
                // todo
//                MessageUtil.error(player, "You can't do that in this plot.");
            }
        }
    }

    private <T extends Event & Cancellable> void checkBlockProtection(Block sourceBlock, Plot sourcePlot, Subplot sourceSubplot, Block target, T event, PlotProtectionType type) {
        WorldConfig worldConfig = worldHandler.getWorld(target.getWorld()).getConfig();
        if (!worldConfig.hasPlots()) {
            return;
        }

        Location targetLoc = target.getLocation();

        Plot blockPlot = worldHandler.getPlotAt(targetLoc);
        if (!blockPlot.hasOwner()) {
            return;
        }

        if (blockPlot.getOwnerId() != sourcePlot.getOwnerId()) {
            // a block in an owned plot is being moved by a piston in a plot which either does not have an owner or
            // is owned by a different group to the owner of the plot the moved block is within - get rid
            PlotProtectionTriggerEvent protectEvent = PoliticsEventFactory.callPlotProtectionTriggerEvent(
                    blockPlot, target, new PlotDamageSource(sourceBlock), type, event);
            if (!protectEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }

        if (!worldConfig.hasSubplots()) {
            return;
        }

        // both piston and invaded blocks are in the same plot - time to check subplots

        Subplot blockSubplot = blockPlot.getSubplotAt(targetLoc).orElse(null);
        if (blockSubplot == null) {
            return;
        }

        if (sourceSubplot == null || !sourceSubplot.getOwnerId().equals(blockSubplot.getOwnerId())) {
            // a block in an owned subplot is being moved by a piston in a subplot which either does not have an
            // owner or is owned by someone other than the other of the plot the moved block is within - cancel
            SubplotProtectionTriggerEvent protectEvent = PoliticsEventFactory.callSubplotProtectionTriggerEvent(
                    blockPlot, blockSubplot, target, new PlotDamageSource(sourceBlock), type, event);
            if (!protectEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    private ProtectionCheck<Plot> checkPlotPrivileges(Player player, Location location, Privilege privilege) {
        if (!(privilege.isOfType(PrivilegeType.PLOT))) {
            throw new IllegalArgumentException("Must be a plot-type privilege");
        }

        Plot plot = worldHandler.getPlotAt(location);
        return new ProtectionCheck<>(plot, plot.can(player, privilege));
    }

    private ProtectionCheck<Subplot> checkSubplotPrivileges(Player player, Location location, Privilege privilege) {
        if (!(privilege.isOfType(PrivilegeType.PLOT))) {
            throw new IllegalArgumentException("Must be a plot-type privilege");
        }

        Plot plot = worldHandler.getPlotAt(location);
        Subplot subplot = plot.getSubplotAt(location).orElse(null);
        if (subplot == null) {
            return new ProtectionCheck<>(null, true);
        }
        return new ProtectionCheck<>(subplot, subplot.can(player, privilege));
    }

    // listener methods which mostly simply make appropriate calls to above logic methods

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        checkPlayerProtection(event.getPlayer(), event.getBlock(), event, PlotProtectionType.BLOCK_BREAK);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        checkPlayerProtection(event.getPlayer(), event.getBlock(), event, PlotProtectionType.BLOCK_PLACE);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        for (BlockState block : event.getReplacedBlockStates()) {
            checkPlayerProtection(event.getPlayer(), block.getBlock(), event, PlotProtectionType.BLOCK_PLACE);

            if (event.isCancelled()) {
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        Block pistonBlock = event.getBlock();
        Location pistonLoc = pistonBlock.getLocation();

        Plot pistonPlot = worldHandler.getPlotAt(pistonLoc);
        Subplot pistonSubplot = pistonPlot.getSubplotAt(pistonLoc).orElse(null);

        // prevent pulling blocks out of plots
        for (Block moved : event.getBlocks()) {
            checkBlockProtection(pistonBlock, pistonPlot, pistonSubplot, moved, event, PlotProtectionType.PISTON_PULL);

            if (event.isCancelled()) {
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Block pistonBlock = event.getBlock();
        Location pistonLoc = pistonBlock.getLocation();

        List<Block> blocks = event.getBlocks();

        Plot pistonPlot = worldHandler.getPlotAt(pistonLoc);
        Subplot pistonSubplot = pistonPlot.getSubplotAt(pistonLoc).orElse(null);

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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        // stop dispensers being used to dispense blocks (e.g. lava) or item between (sub)plots
        Block dispenserBlock = event.getBlock();
        Location dispenserLoc = dispenserBlock.getLocation();
        Dispenser dispenser = new Dispenser(Material.DISPENSER, dispenserBlock.getData());

        Plot sourcePlot = worldHandler.getPlotAt(dispenserLoc);
        Subplot sourceSubplot = sourcePlot.getSubplotAt(dispenserLoc).orElse(null);

        Block targetBlock = dispenserBlock.getRelative(dispenser.getFacing());

        checkBlockProtection(dispenserBlock, sourcePlot, sourceSubplot, targetBlock, event, PlotProtectionType.DISPENSER);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        // protect from things like lava spreading into a plot or subplot from outside
        Block sourceBlock = event.getBlock();
        Location sourceLoc = sourceBlock.getLocation();

        Plot sourcePlot = worldHandler.getPlotAt(sourceLoc);
        Subplot sourceSubplot = sourcePlot.getSubplotAt(sourceLoc).orElse(null);

        Block targetBlock = event.getToBlock();

        checkBlockProtection(sourceBlock, sourcePlot, sourceSubplot, targetBlock, event, PlotProtectionType.BLOCK_FLOW);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        // placing a liquid (from a bucket) is effectively placing a block
        checkPlayerProtection(event.getPlayer(), event.getBlockClicked(), event, PlotProtectionType.BLOCK_PLACE);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        // removing a block (into a bucket) is effectively breaking a block
        checkPlayerProtection(event.getPlayer(), event.getBlockClicked(), event, PlotProtectionType.BLOCK_BREAK);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
        // todo prevent breaks in plots or subplots by non-owners just like normal blocks
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        // todo prevent placement in plots or subplots by non-owners just like normal blocks
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockForm(BlockFormEvent event) {
        // todo check if in a plot
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        // todo make configurable whether to prevent this
        for (BlockState block : event.getBlocks()) {
            // todo prevent trees outside plots growing into a plot
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            // todo check if this is allowed
        }

        // todo also check if burning is allowed etc
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        // todo check plot of burning block
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        // todo make configurable whether this is enabled
        if (event.getSource().getType() != Material.FIRE) {
            return;
        }

        // todo check if in a plot
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityFormBlock(EntityBlockFormEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            // todo prevent inside plot/subplot not owned by the player
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLightningStrike(LightningStrikeEvent event) {
        // todo deal with lightning strikes caused by players
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        // todo deal with the following:
        // endermen, silverfish, rabbits (eating crops), wither (if explosions not allowed)
        // entities or non-owner players trampling on crops
        // breaking lilypads by collision with boat with a player driver who does not have permission to break here
        // falling blocks -> sand cannons, but also directly downwards falling blocks as subplots may be below others
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityBreakDoor(EntityBreakDoorEvent event) {
        // todo prevent entities from breaking doors in plots and subplots if configured to do so
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        // todo prevent non-player entities trampling crops in plots / subplots
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        // todo prevent creeper etc explosions in plots and subplots if configured to do so
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        // todo prevent TNT etc explosions in plots and subplots if configured to do so
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // todo prevent spawn eggs being used in plots or subplots if not permitted to do so

        // todo prevent all monster spawns in plots or subplots if configured to do so
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        // todo prevent animals in plots or subplots being harmed by non-owners
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
        // todo prevent animals in plots or subplots being combusted by non-owners
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onVehicleDamage(VehicleDamageEvent event) {
        // todo prevent vehicles in plots being damaged
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        // todo prevent harm to animals inside plots / subplots if configured to do so
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // todo prevent taking items from armor stands, item frames etc in someone else's plot/subplot
    }

    public static final class ProtectionCheck<T extends ProtectedRegionCuboid> {
        private final T checked;
        private final boolean result;

        ProtectionCheck(T checked, boolean result) {
            this.checked = checked;
            this.result = result;
        }

        public T getChecked() {
            return checked;
        }

        public boolean getResult() {
            return result;
        }
    }
}
