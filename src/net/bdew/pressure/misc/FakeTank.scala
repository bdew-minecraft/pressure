/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.misc

import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack, FluidTankInfo, IFluidHandler}

/**
 * Mixin that makes a TE look like a tank but not do anything. Used to make pipes from other mods connect correctly.
 */
trait FakeTank extends IFluidHandler {
  def isValidDirectionForFakeTank(dir: ForgeDirection): Boolean
  override def getTankInfo(from: ForgeDirection) =
    if (isValidDirectionForFakeTank(from))
      Array(new FluidTankInfo(null, Int.MaxValue))
    else
      null
  override def canDrain(from: ForgeDirection, fluid: Fluid) = false
  override def canFill(from: ForgeDirection, fluid: Fluid) = false
  override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean) = null
  override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean) = null
  override def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean) = 0
}
