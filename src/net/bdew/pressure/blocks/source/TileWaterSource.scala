/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.source

import net.bdew.lib.capabilities.legacy.OldFluidHandlerEmulator
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.pressure.misc.FakeFluidHandler
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids._
import net.minecraftforge.fluids.capability.{FluidTankProperties, IFluidTankProperties}

class TileWaterSource extends TileEntity with CapabilityProvider with OldFluidHandlerEmulator {
  val fullStack = new FluidStack(FluidRegistry.WATER, Int.MaxValue)

  addCapability(Capabilities.CAP_FLUID_HANDLER, new FakeFluidHandler {
    override def getTankProperties: Array[IFluidTankProperties] = Array(new FluidTankProperties(fullStack, Int.MaxValue, false, true))
    override def canDrain: Boolean = true
    override def canDrainFluidType(fluidStack: FluidStack): Boolean = fluidStack.getFluid == FluidRegistry.WATER

    override def drain(resource: FluidStack, doDrain: Boolean): FluidStack =
      if (resource.getFluid == FluidRegistry.WATER)
        new FluidStack(FluidRegistry.WATER, resource.amount)
      else null

    override def drain(maxDrain: Int, doDrain: Boolean): FluidStack =
      new FluidStack(FluidRegistry.WATER, maxDrain)
  })
}
