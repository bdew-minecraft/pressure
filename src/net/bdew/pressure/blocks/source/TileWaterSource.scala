/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.blocks.source

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids._
import net.minecraftforge.common.util.ForgeDirection

class TileWaterSource extends TileEntity with IFluidHandler {
  val fullStack = new FluidStack(FluidRegistry.WATER, Int.MaxValue)
  override def getTankInfo(from: ForgeDirection) = Array(new FluidTankInfo(fullStack, Int.MaxValue))
  override def canDrain(from: ForgeDirection, fluid: Fluid) = fluid == FluidRegistry.WATER
  override def canFill(from: ForgeDirection, fluid: Fluid) = false

  override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean) =
    new FluidStack(FluidRegistry.WATER, maxDrain)

  override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean) =
    if (resource.getFluid == FluidRegistry.WATER)
      new FluidStack(FluidRegistry.WATER, resource.amount)
    else null

  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean) = 0
}
