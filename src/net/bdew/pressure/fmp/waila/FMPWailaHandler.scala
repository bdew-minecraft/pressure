/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.waila

import mcp.mobius.waila.api.IWailaRegistrar
import net.bdew.pressure.Pressure

object FMPWailaHandler {
  def loadCallback(reg: IWailaRegistrar) {
    Pressure.logInfo("WAILA callback received in FMP module, loading...")
    reg.registerBodyProvider(WailaValveFMPHandler, "bdew.pressure.checkvalve")
    reg.registerBodyProvider(WailaValveFMPHandler, "bdew.pressure.pipesensor")
  }
}
