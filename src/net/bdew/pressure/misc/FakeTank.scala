/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.misc

import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.{Fluid, FluidStack, FluidTankInfo, IFluidHandler}

/**
  * Mixin that makes a TE look like a tank but not do anything. Used to make pipes from other mods connect correctly.
  */
trait FakeTank extends IFluidHandler {
  def isValidDirectionForFakeTank(dir: EnumFacing): Boolean

  override def getTankInfo(from: EnumFacing) =
    if (isValidDirectionForFakeTank(from))
      Array(new FluidTankInfo(null, Int.MaxValue))
    else
      Array.empty

  override def canDrain(from: EnumFacing, fluid: Fluid) = false
  override def canFill(from: EnumFacing, fluid: Fluid) = false
  override def drain(from: EnumFacing, maxDrain: Int, doDrain: Boolean) = null
  override def drain(from: EnumFacing, resource: FluidStack, doDrain: Boolean) = null
  override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean) = 0
}
