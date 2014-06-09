/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.blocks.output

import net.bdew.lib.data.base.TileDataSlots
import net.bdew.pressure.api.IPressureEject
import net.minecraftforge.fluids.{IFluidHandler, FluidStack}
import net.bdew.pressure.config.Blocks
import net.bdew.pressure.misc.{FakeTank, BlockRef}
import net.bdew.pressure.blocks.TileFilterable
import net.minecraftforge.common.ForgeDirection

class TileOutput extends TileDataSlots with FakeTank with IPressureEject with TileFilterable {
  def getFacing = Blocks.output.getFacing(worldObj, xCoord, yCoord, zCoord)
  
  lazy val me = BlockRef.fromTile(this)

  override def eject(resource: FluidStack, doEject: Boolean) = {
    if (isFluidAllowed(resource)) {
      val f = getFacing
      me.neighbour(f).getTile[IFluidHandler] map { dest =>
        dest.fill(f.getOpposite, resource, doEject)
      } getOrElse 0
    } else 0
  }

  override def getXCoord = xCoord
  override def getYCoord = yCoord
  override def getZCoord = zCoord
  override def getWorld = worldObj

  override def isValidDirectionForFakeTank(dir: ForgeDirection) = getFacing == dir
}

