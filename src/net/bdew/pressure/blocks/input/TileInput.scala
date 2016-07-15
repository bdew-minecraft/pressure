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
import net.bdew.lib.capabilities.helpers.FluidHelper
import net.bdew.lib.capabilities.legacy.OldFluidHandlerEmulator
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.data.base.TileDataSlotsTicking
import net.bdew.pressure.api._
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.misc.FakeFluidHandler
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack

class TileInput extends TileDataSlotsTicking with IPressureInject with TileFilterable with CapabilityProvider with OldFluidHandlerEmulator {
  def getFacing = BlockInput.getFacing(worldObj, pos)
  var connection: IPressureConnection = null

  val handler = new FakeFluidHandler {
    override def canFill: Boolean = true
    override def canFillFluidType(fluidStack: FluidStack): Boolean = isFluidAllowed(fluidStack)
    override def fill(resource: FluidStack, doFill: Boolean): Int = pushFluid(resource, doFill)
  }

  addCapabilityOption(Capabilities.CAP_FLUID_HANDLER) { side =>
    if (side == getFacing.getOpposite)
      Some(handler)
    else
      None
  }

  def pushFluid(resource: FluidStack, doPush: Boolean): Int = {
    if (worldObj.isRemote) {
      if (resource != null && resource.getFluid != null && resource.amount > 0 && isFluidAllowed(resource.getFluid))
        resource.amount
      else
        0
    } else if (resource != null && resource.getFluid != null && resource.amount > 0 && isFluidAllowed(resource.getFluid)) {
      if (connection == null && Helper.canPipeConnectTo(worldObj, pos.offset(getFacing), getFacing.getOpposite))
        connection = Helper.recalculateConnectionInfo(this, getFacing)
      if (connection != null)
        connection.pushFluid(resource, doPush)
      else
        0
    } else 0
  }

  serverTick.listen(() => {
    if (BlockInput.getSignal(worldObj, pos)) {
      FluidHelper.getFluidHandler(worldObj, pos.offset(getFacing.getOpposite), getFacing) map { from =>
        FluidHelper.pushFluid(from, handler, true)
      }
    }
  })

  override def invalidateConnection(direction: EnumFacing) = connection = null

  override def pressureNodePos = getPos
  override def pressureNodeWorld = getWorld
}
