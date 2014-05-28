/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.blocks.output

import net.bdew.pressure.blocks.{BlockNotifyUpdates, BaseIOBlock}
import net.bdew.pressure.api.IPressureConnectableBlock
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.ForgeDirection

class BlockOutput(id: Int) extends BaseIOBlock(id, "output", classOf[TileOutput]) with BlockNotifyUpdates with IPressureConnectableBlock {
  override def canConnectFrom(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    getFacing(world, x, y, z) == side.getOpposite
}
