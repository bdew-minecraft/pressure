/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.enderio

import net.bdew.pressure.api.properties.IFilterable
import net.bdew.pressure.compat.enderio.EnderIOReflect._
import net.minecraftforge.fluids.{Fluid, FluidStack}

import scala.language.reflectiveCalls

class FilterableProxy(val b: TileConduitBundle, cl: Class[_ <: LiquidConduit]) extends IFilterable {
  override def setFluidFilter(fluid: Fluid) {
    for {
      cond <- Option(b.getConduit(cl))
      net <- Option(cond.getNetwork)
    } {
      net.setFluidType(new FluidStack(fluid, 1))
      net.setFluidTypeLocked(true)
    }
  }

  override def clearFluidFilter() {
    for {
      cond <- Option(b.getConduit(cl))
      net <- Option(cond.getNetwork)
    } {
      net.setFluidTypeLocked(false)
    }
  }
}
