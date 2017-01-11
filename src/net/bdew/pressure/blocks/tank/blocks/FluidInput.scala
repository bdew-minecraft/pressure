/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.capabilities.helpers.FluidMultiHandler
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.multiblock.interact.CIFluidInput
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.blocks.tank.BaseModule

object BlockFluidInput extends BaseModule("tank_fluid_input", "FluidInput", classOf[TileFluidInput])

class TileFluidInput extends TileModule with CapabilityProvider {
  val kind: String = "FluidInput"

  override def getCore = getCoreAs[CIFluidInput]

  addCapabilityOption(Capabilities.CAP_FLUID_HANDLER) { side =>
    getCore.map(core => FluidMultiHandler.wrap(core.getInputTanks))
  }
}
