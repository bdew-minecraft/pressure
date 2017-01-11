/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.capabilities.CapabilityProvider
import net.bdew.lib.data.DataSlotOption
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.pressure.api.PressureAPI
import net.bdew.pressure.api.properties.IFilterable
import net.minecraftforge.fluids.{Fluid, FluidStack}

trait TileFilterable extends TileDataSlots with CapabilityProvider {
  val fluidFilter = new DataSlotOption[Fluid]("fluidFilter", this).setUpdate(UpdateKind.SAVE, UpdateKind.WORLD, UpdateKind.RENDER)

  object FilterableImpl extends IFilterable {
    def setFluidFilter(fluid: Fluid) = if (fluid == null) fluidFilter.unset() else fluidFilter.set(fluid)
    def clearFluidFilter() = fluidFilter.unset()
  }

  addCapability(PressureAPI.FILTERABLE, FilterableImpl)

  def isFluidAllowed(fluid: Fluid): Boolean =
    fluid != null && (fluidFilter.isEmpty || fluidFilter.contains(fluid))

  def isFluidAllowed(fs: FluidStack): Boolean =
    fs != null && isFluidAllowed(fs.getFluid)

  def getFluidFilter = fluidFilter.value
}
