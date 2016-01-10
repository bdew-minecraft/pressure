/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.api;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IPressureHelper extends IPressureExtension {
    /**
     * Forces a reinitialization of the connection. Call before pushing if your current connection object is null.
     * Should only be called on server side
     *
     * @param te   Origin tile entity
     * @param side Side of the connection
     * @return new connection object
     */
    IPressureConnection recalculateConnectionInfo(IPressureInject te, EnumFacing side);

    /**
     * Notify system of block change (call on server for any pipes/inputs/outputs placed / broken / rotated, etc.
     */
    void notifyBlockChanged(World world, BlockPos pos);

    /**
     * Register a new extension (allows other mods to provide overrides to some logic)
     */
    void registerExtension(IPressureExtension extension);

    /**
     * Register a IFilterable provider (allows configurator to work with other mods blocks)
     */
    void registerIFilterableProvider(IFilterableProvider provider);
}
