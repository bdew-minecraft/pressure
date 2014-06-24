/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.blocks.pump

import net.minecraft.world.World
import net.bdew.pressure.misc.{FakeTank, BlockRef}
import net.minecraftforge.fluids.IFluidHandler
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.lib.data.base.TileDataSlots
import net.minecraftforge.common.util.ForgeDirection
import net.minecraft.block.Block

class TilePump extends TileDataSlots with FakeTank with TileFilterable {

  override def shouldRefresh(oldBlock: Block, newBlock: Block, oldMeta: Int, newMeta: Int, world: World, x: Int, y: Int, z: Int) =
    oldBlock != newBlock

  def getFacing = BlockPump.getFacing(worldObj, xCoord, yCoord, zCoord)

  lazy val me = BlockRef.fromTile(this)

  serverTick.listen(doPushFluid)

  def doPushFluid() {
    if ((me.meta.getOrElse(0) & 8) == 0) return
    val face = getFacing
    for (from <- me.neighbour(face.getOpposite).getTile[IFluidHandler];
         to <- me.neighbour(face).getTile[IFluidHandler]) {
      val res = from.drain(face.getOpposite, Int.MaxValue, false)
      if (res != null && res.getFluid != null && res.amount > 0 && isFluidAllowed(res)) {
        val filled = to.fill(face.getOpposite, res, true)
        if (filled > 0)
          from.drain(face, filled, true)
      }
    }
  }

  override def isValidDirectionForFakeTank(dir: ForgeDirection) = dir == getFacing || dir.getOpposite == getFacing
}
