/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.api;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Extension of the pipe system to allow interoperability with other mods
 */
public interface IPressureExtension {
    /**
     * Checks if pipe can connect to a given block
     *
     * @return true if connection is possible
     */
    boolean canPipeConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection side);

    /**
     * Checks if pipe can connect on a given side
     *
     * @return true if connection is possible and the block is a valid pipe
     */
    boolean canPipeConnectFrom(IBlockAccess world, int x, int y, int z, ForgeDirection side);

    /**
     * @return true if block is valid for pipe connections (including other pipes)
     */
    boolean isConnectableBlock(IBlockAccess world, int x, int y, int z);

    /**
     * @return true if pressure network connections can pass through this block
     */
    boolean isTraversableBlock(IBlockAccess world, int x, int y, int z);

    /**
     * Attempt to place a block at the given coordinates, called on the server
     * Implement to allow alternative presentations of blocks from the mod (eg. FMP parts)
     *
     * @return true if block was placed successfully
     */
    boolean tryPlaceBlock(World w, int x, int y, int z, Block block, EntityPlayerMP p);
}
