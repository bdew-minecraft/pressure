/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.api;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Implemented on all blocks that can connect to Pressure pipes or blocks
 */
public interface IPressureConnectableBlock {
    /**
     * Checks if pipe can connect from side
     *
     * @return true if connection is possible
     */
    boolean canConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection side);
}
