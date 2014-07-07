/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.blocks

import net.bdew.lib.data.DataSlotString
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.minecraftforge.fluids.{Fluid, FluidStack}

trait TileFilterable extends TileDataSlots {
  val fluidFilter = new DataSlotString("fluidFilter", this).setUpdate(UpdateKind.SAVE, UpdateKind.WORLD, UpdateKind.RENDER)

  def isFluidAllowed(fluid: Fluid): Boolean =
    fluid != null && ((fluidFilter :== null) || fluid.getName.equals(fluidFilter.cval))

  def isFluidAllowed(fs: FluidStack): Boolean =
    fs != null && isFluidAllowed(fs.getFluid)
}
