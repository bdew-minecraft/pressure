/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.waila

import mcp.mobius.waila.api.IWailaRegistrar
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.Pressure
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.blocks.input.TileInput
import net.bdew.pressure.blocks.output.TileOutput
import net.bdew.pressure.blocks.pump.TilePump
import net.bdew.pressure.blocks.tank.controller.TileTankController

object WailaHandler {
  def loadCallback(reg: IWailaRegistrar) {
    Pressure.logInfo("WAILA callback received, loading...")
    reg.registerBodyProvider(WailaTankProvider, classOf[TileTankController])
    reg.registerBodyProvider(WailaTankModuleProvider, classOf[TileModule])
    reg.registerBodyProvider(WailaFilterableHandler, classOf[TileFilterable])
    reg.registerBodyProvider(WailaPumpHandler, classOf[TilePump])
    reg.registerBodyProvider(WailaPressureOutputHandler, classOf[TileOutput])
    reg.registerBodyProvider(WailaPressureInputHandler, classOf[TileInput])
  }
}
