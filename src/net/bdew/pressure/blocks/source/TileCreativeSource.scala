/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.source


import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.misc.FakeFluidHandler
import net.minecraftforge.fluids._
import net.minecraftforge.fluids.capability.{FluidTankProperties, IFluidTankProperties}

class TileCreativeSource extends TileFilterable with CapabilityProvider {
  def stack(amount: Int = Int.MaxValue) = getFluidFilter.map(new FluidStack(_, amount)).orNull

  addCapability(Capabilities.CAP_FLUID_HANDLER, new FakeFluidHandler {
    override def getTankProperties: Array[IFluidTankProperties] = Array(new FluidTankProperties(stack(Int.MaxValue), Int.MaxValue, false, true))

    override def canDrain: Boolean = true
    override def canDrainFluidType(fluidStack: FluidStack): Boolean = getFluidFilter.contains(fluidStack.getFluid)

    override def drain(resource: FluidStack, doDrain: Boolean): FluidStack =
      if (canDrainFluidType(resource)) resource else null

    override def drain(maxDrain: Int, doDrain: Boolean): FluidStack =
      stack(maxDrain)
  })
}
