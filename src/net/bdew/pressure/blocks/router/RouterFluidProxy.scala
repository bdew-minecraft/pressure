/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router

import net.bdew.pressure.blocks.router.data.RouterSideMode
import net.bdew.pressure.misc.FakeFluidHandler
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack

class RouterFluidProxy(router: TileRouter, side: EnumFacing) extends FakeFluidHandler {
  override def canFill: Boolean = RouterSideMode.inputs.contains(router.sideModes.get(side)) && router.canWorkWithRsMode(router.sideControl.get(side))
  override def canFillFluidType(fluidStack: FluidStack): Boolean = router.isSideValidIO(side, fluidStack, RouterSideMode.inputs)
  override def fill(resource: FluidStack, doFill: Boolean): Int =
    if (resource != null && router.isSideValidIO(side, resource, RouterSideMode.inputs))
      router.distributeFluid(resource, doFill)
    else
      0
}