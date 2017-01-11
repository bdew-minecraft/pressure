/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.misc

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.{IFluidHandler, IFluidTankProperties}

class FakeFluidHandler extends IFluidHandler with IFluidTankProperties {
  override def getContents: FluidStack = null
  override def getCapacity: Int = 0

  override def canFill: Boolean = false
  override def canDrain: Boolean = false
  override def canFillFluidType(fluidStack: FluidStack): Boolean = false
  override def canDrainFluidType(fluidStack: FluidStack): Boolean = false

  override def getTankProperties: Array[IFluidTankProperties] = Array(this)

  override def fill(resource: FluidStack, doFill: Boolean): Int = 0
  override def drain(resource: FluidStack, doDrain: Boolean): FluidStack = null
  override def drain(maxDrain: Int, doDrain: Boolean): FluidStack = null
}

