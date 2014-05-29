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
import net.minecraftforge.common.ForgeDirection
import net.bdew.pressure.misc.{FakeTank, Helper, BlockRef}
import net.bdew.pressure.config.Blocks
import net.minecraftforge.fluids.{FluidStack, Fluid}
import net.bdew.pressure.blocks.TileFilterable

class TileInput extends TileDataSlots with FakeTank with IPressureInject with TileFilterable {
  def facing = Blocks.output.getFacing(worldObj, xCoord, yCoord, zCoord)
  lazy val me = BlockRef.fromTile(this)
  var connection: IConnectionInfo = null

  override def canFill(from: ForgeDirection, fluid: Fluid) = from == facing.getOpposite && isFluidAllowed(fluid)

  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean): Int = {
    if (resource != null && resource.getFluid != null && resource.amount > 0 && canFill(from, resource.getFluid)) {
      if (connection == null && me.neighbour(facing).getBlock[IPressureConnectableBlock].isDefined)
        connection = Helper.recalculateConnectionInfo(this, facing)
      if (connection != null)
        return Helper.pushFluidIntoPressureSytem(connection, resource, doFill)
    }
    return 0
  }

  override def invalidateConnection() = connection = null

  override def getZCoord = zCoord
  override def getYCoord = yCoord
  override def getXCoord = xCoord
  override def getWorld = worldObj
}
