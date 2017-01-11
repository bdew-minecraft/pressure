/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.api;

import net.minecraftforge.fluids.FluidStack;

/**
 * Represents a connection from an input TileEntity to the pressure system
 * You can obtain a copy from IPressureHelper.recalculateConnectionInfo
 */
public interface IPressureConnection {
    /**
     * Tries to push fluid into the system. Call only on server side.
     *
     * @param fluid FluidStack to push
     * @return how much fluid was actually pushed
     */
    int pushFluid(FluidStack fluid, boolean doPush);
}
