/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.api;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

public interface IPressureHelper extends IPressureExtension {
    /**
     * Forces a reinitialization of the connection. Call before pushing if your current connection object is null
     *
     * @param te   Origin tile entity
     * @param side Side of the connection
     * @return new connection object
     */
    IConnectionInfo recalculateConnectionInfo(IPressureInject te, ForgeDirection side);

    /**
     * Tries to push fluid into the system
     *
     * @param fluid      FluidStack to push
     * @param connection Connection object
     * @return how much fluid was actualy pushed
     */
    int pushFluidIntoPressureSytem(IConnectionInfo connection, FluidStack fluid, boolean doPush);

    /**
     * Notify system of block change (call on server for any pipes/inputs/outputs placed / broken / rotated, etc.
     */
    void notifyBlockChanged(World world, int x, int y, int z);

    /**
     * Register a new extension (allows other mods to provide ovverides to some logic)
     */
    void registerExtension(IPressureExtension extension);
}
