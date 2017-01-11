/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import net.bdew.lib.capabilities.CapabilityProvider
import net.bdew.lib.multiblock.tile.{TileController, TileModule}
import net.bdew.pressure.api.PressureAPI
import net.bdew.pressure.api.properties.IFilterable
import net.minecraftforge.fluids.Fluid

trait CIFilterable extends TileController {
  def filterableCapability: IFilterable
  def getFluidFilter: Option[Fluid]
}

trait MIFilterable extends TileModule with CapabilityProvider {
  override def getCore = getCoreAs[CIFilterable]

  addCapabilityOption(PressureAPI.FILTERABLE) { face =>
    getCore map (_.filterableCapability)
  }
}
