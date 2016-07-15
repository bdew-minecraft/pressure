/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.capabilities.helpers.FluidMultiHandler
import net.bdew.lib.capabilities.legacy.OldFluidHandlerEmulator
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.multiblock.interact.{CIFluidInput, CIFluidOutput}
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.blocks.tank.BaseModule

object BlockFluidAccess extends BaseModule("TankFluidAccess", "FluidAccess", classOf[TileFluidAccess])

class TileFluidAccess extends TileModule with CapabilityProvider with OldFluidHandlerEmulator {
  val kind: String = "FluidAccess"

  override def getCore = getCoreAs[CIFluidInput with CIFluidOutput]

  addCapabilityOption(Capabilities.CAP_FLUID_HANDLER) { side =>
    getCore.map(core => FluidMultiHandler.wrap(core.getOutputTanks ++ core.getInputTanks))
  }
}
