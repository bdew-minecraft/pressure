/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.pressurenet

import net.bdew.lib.PimpVanilla._
import net.bdew.pressure.api.{IFilterableProvider, IPressureConnectableBlock, IPressureExtension, PressureAPI}
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}

object InternalPressureExtension extends IPressureExtension with IFilterableProvider {
  override def canPipeConnectTo(w: IBlockAccess, pos: BlockPos, side: EnumFacing) =
    w.getBlockSafe[IPressureConnectableBlock](pos) exists {
      _.canConnectTo(w, pos, side)
    }

  override def canPipeConnectFrom(w: IBlockAccess, pos: BlockPos, side: EnumFacing) = isConnectableBlock(w, pos)

  override def isConnectableBlock(w: IBlockAccess, pos: BlockPos) =
    w.getBlockSafe[IPressureConnectableBlock](pos).isDefined

  override def isTraversableBlock(world: IBlockAccess, pos: BlockPos) =
    world.getBlockSafe[IPressureConnectableBlock](pos) exists {
      _.isTraversable(world, pos)
    }

  override def tryPlaceBlock(w: World, pos: BlockPos, b: Block, p: EntityPlayer, reallyPlace: Boolean) = {
    if (w.isAirBlock(pos) || w.getBlockState(pos).getBlock.isReplaceable(w, pos)) {
      if (reallyPlace) {
        val st = b.onBlockPlaced(w, pos, EnumFacing.UP, 0, 0, 0, 0, p)
        if (w.setBlockState(pos, st, 3)) {
          b.onBlockPlacedBy(w, pos, st, p, new ItemStack(b))
          true
        } else false // Failed to place
      } else true // Just testing, not really placing
    } else false // Can't place
  }

  override def getFilterableForWorldCoordinates(world: World, pos: BlockPos, side: EnumFacing) = {
    world.getCapSafe(pos, side, PressureAPI.FILTERABLE).orNull
  }
}
