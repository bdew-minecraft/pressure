/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Implemented on all blocks that can connect to Pressure pipes or blocks
 */
public interface IPressureConnectableBlock {
    /**
     * Checks if pipe can connect from side
     *
     * @return true if connection is possible
     */
    boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing side);

    /**
     * Checks if network connections should pass through this block
     * Blocks that can be part of separate pressure networks should return false
     *
     * @return true if network connections should pass through this block
     */
    boolean isTraversable(IBlockAccess world, BlockPos pos);
}
