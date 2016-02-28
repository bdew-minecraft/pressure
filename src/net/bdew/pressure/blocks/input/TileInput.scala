/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.input

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.data.base.TileDataSlotsTicking
import net.bdew.pressure.api._
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.misc.FakeTank
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.{Fluid, FluidStack, IFluidHandler}

class TileInput extends TileDataSlotsTicking with FakeTank with IPressureInject with TileFilterable {
  def getFacing = BlockInput.getFacing(worldObj, pos)
  var connection: IPressureConnection = null

  override def canFill(from: EnumFacing, fluid: Fluid) = from == getFacing.getOpposite && isFluidAllowed(fluid)

  override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean): Int = {
    if (worldObj.isRemote) {
      if (resource != null && resource.getFluid != null && resource.amount > 0 && canFill(from, resource.getFluid))
        return resource.amount
    } else if (resource != null && resource.getFluid != null && resource.amount > 0 && canFill(from, resource.getFluid)) {
      if (connection == null && Helper.canPipeConnectTo(worldObj, pos.offset(getFacing), getFacing.getOpposite))
        connection = Helper.recalculateConnectionInfo(this, getFacing)
      if (connection != null)
        return connection.pushFluid(resource, doFill)
    }
    return 0
  }

  serverTick.listen(doPushFluid)

  def doPushFluid() {
    if (!BlockInput.getSignal(worldObj, pos)) return
    val face = getFacing
    worldObj.getTileSafe[IFluidHandler](pos.offset(face.getOpposite)).foreach { from =>
      val res = from.drain(face, Int.MaxValue, false)
      if (res != null && res.getFluid != null && res.amount > 0 && isFluidAllowed(res)) {
        val filled = fill(face.getOpposite, res, true)
        if (filled > 0)
          from.drain(face, filled, true)
      }
    }
  }

  override def invalidateConnection(direction: EnumFacing) = connection = null
  override def isValidDirectionForFakeTank(dir: EnumFacing) = dir == getFacing.getOpposite

  override def pressureNodePos = getPos
  override def pressureNodeWorld = getWorld
}
