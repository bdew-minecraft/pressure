/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.source

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids._

class TileWaterSource extends TileEntity with IFluidHandler {
  val fullStack = new FluidStack(FluidRegistry.WATER, Int.MaxValue)
  override def getTankInfo(from: EnumFacing) = Array(new FluidTankInfo(fullStack, Int.MaxValue))
  override def canDrain(from: EnumFacing, fluid: Fluid) = fluid == FluidRegistry.WATER
  override def canFill(from: EnumFacing, fluid: Fluid) = false

  override def drain(from: EnumFacing, maxDrain: Int, doDrain: Boolean) =
    new FluidStack(FluidRegistry.WATER, maxDrain)

  override def drain(from: EnumFacing, resource: FluidStack, doDrain: Boolean) =
    if (resource.getFluid == FluidRegistry.WATER)
      new FluidStack(FluidRegistry.WATER, resource.amount)
    else null

  override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean) = 0
}
