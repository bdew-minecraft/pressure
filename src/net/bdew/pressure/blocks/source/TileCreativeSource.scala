/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.source

import net.bdew.pressure.blocks.TileFilterable
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids._

class TileCreativeSource extends TileFilterable with IFluidHandler {
  def stack(amount: Int = Int.MaxValue) = getFluidFilter.map(new FluidStack(_, amount)).orNull

  override def getTankInfo(from: EnumFacing) = Array(new FluidTankInfo(stack(), Int.MaxValue))
  override def canDrain(from: EnumFacing, fluid: Fluid) = getFluidFilter.contains(fluid)
  override def canFill(from: EnumFacing, fluid: Fluid) = false

  override def drain(from: EnumFacing, maxDrain: Int, doDrain: Boolean) = stack(maxDrain)
  override def drain(from: EnumFacing, resource: FluidStack, doDrain: Boolean) =
    if (canDrain(from, resource.getFluid)) resource else null

  override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean) = 0
}
