/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.drain

import net.bdew.lib.capabilities.legacy.OldFluidHandlerEmulator
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.data.DataSlotTank
import net.bdew.lib.data.base.TileDataSlotsTicking
import net.bdew.pressure.api.IPressureEject
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.misc.FakeFluidHandler
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.{Fluid, FluidStack}

class TileSluice extends TileDataSlotsTicking with CapabilityProvider with OldFluidHandlerEmulator with IPressureEject with TileFilterable {
  def getFacing = BlockSluice.getFacing(worldObj, pos)

  val bufferTank = new DataSlotTank("buffer", this, Fluid.BUCKET_VOLUME)

  val handler = new FakeFluidHandler {
    override def canFill: Boolean = true
    override def canFillFluidType(fluidStack: FluidStack): Boolean = isFluidAllowed(fluidStack) && fluidStack.getFluid.canBePlacedInWorld
    override def fill(resource: FluidStack, doFill: Boolean): Int = {
      if (resource != null && resource.getFluid != null && resource.amount > 0 && canFillFluidType(resource)) {
        if (bufferTank.getFluid != null && bufferTank.getFluid.getFluid != resource.getFluid)
          bufferTank.setFluid(null)
        val amountFilled = bufferTank.fill(resource, doFill)
        if (doFill && !worldObj.isRemote && bufferTank.getFluidAmount >= Fluid.BUCKET_VOLUME) {
          val target = pos.offset(getFacing)
          if (worldObj.isAirBlock(target)) {
            worldObj.setBlockState(target, bufferTank.getFluid.getFluid.getBlock.getDefaultState, 3)
            worldObj.notifyBlockOfStateChange(target, BlockSluice)
            bufferTank.drain(Fluid.BUCKET_VOLUME, true)
          }
        }
        amountFilled
      } else 0
    }
  }

  addCapabilityOption(Capabilities.CAP_FLUID_HANDLER) { side =>
    if (side == getFacing.getOpposite)
      Some(handler)
    else
      None
  }

  override def eject(resource: FluidStack, direction: EnumFacing, doEject: Boolean) = handler.fill(resource, doEject)

  override def pressureNodePos = getPos
  override def pressureNodeWorld = getWorld
}

