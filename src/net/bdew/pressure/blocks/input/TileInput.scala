/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.blocks.input

import net.bdew.lib.data.base.TileDataSlots
import net.bdew.pressure.api._
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.misc.{BlockRef, FakeTank, Helper}
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack}

class TileInput extends TileDataSlots with FakeTank with IPressureInject with TileFilterable {
  def getFacing = BlockInput.getFacing(worldObj, xCoord, yCoord, zCoord)
  lazy val me = BlockRef.fromTile(this)
  var connection: IPressureConnection = null

  override def canFill(from: ForgeDirection, fluid: Fluid) = from == getFacing.getOpposite && isFluidAllowed(fluid)

  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean): Int = {
    if (worldObj.isRemote) {
      if (resource != null && resource.getFluid != null && resource.amount > 0 && canFill(from, resource.getFluid))
        resource.amount
      else
        0
    } else if (resource != null && resource.getFluid != null && resource.amount > 0 && canFill(from, resource.getFluid)) {
      if (connection == null && Helper.canPipeConnectTo(me.neighbour(getFacing), getFacing.getOpposite))
        connection = Helper.recalculateConnectionInfo(this, getFacing)
      if (connection != null)
        return connection.pushFluid(resource, doFill)
    }
    return 0
  }

  override def invalidateConnection() = connection = null

  override def getZCoord = zCoord
  override def getYCoord = yCoord
  override def getXCoord = xCoord
  override def getWorld = worldObj

  override def isValidDirectionForFakeTank(dir: ForgeDirection) = dir == getFacing.getOpposite
}
