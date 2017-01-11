/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.output

import net.bdew.pressure.api.IPressureConnectableBlock
import net.bdew.pressure.blocks.{BaseIOBlock, BlockNotifyUpdates}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

object BlockOutput extends BaseIOBlock("output", classOf[TileOutput]) with BlockNotifyUpdates with IPressureConnectableBlock {
  override def canConnectTo(world: IBlockAccess, pos: BlockPos, side: EnumFacing) =
    getFacing(world, pos) == side.getOpposite
  override def isTraversable(world: IBlockAccess, pos: BlockPos) = false
}
