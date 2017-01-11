/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.sensor

import net.bdew.lib.gui.Texture
import net.bdew.lib.render.IconPreloader
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object Icons extends IconPreloader {

  trait Loader {
    def iconName: String
    @SideOnly(Side.CLIENT)
    def texture: Texture = map(iconName)
  }

  val clear = TextureLoc("pressure:sensor/null")
  val disabled = TextureLoc("pressure:sensor/disabled")
  val tank = TextureLoc("pressure:sensor/tank")
  val fillEmpty = TextureLoc("pressure:sensor/fill_0")
  val fillFull = TextureLoc("pressure:sensor/fill_100")
  val fillNotEmpty = TextureLoc("pressure:sensor/fill_not_empty")
  val fillNotFull = TextureLoc("pressure:sensor/fill_not_full")
  val fill5 = TextureLoc("pressure:sensor/fill_5")
  val fill25 = TextureLoc("pressure:sensor/fill_25")
  val fill50 = TextureLoc("pressure:sensor/fill_50")
  val fill75 = TextureLoc("pressure:sensor/fill_75")
  val fill95 = TextureLoc("pressure:sensor/fill_95")
  val flow = TextureLoc("pressure:sensor/flow")
  val fluid = TextureLoc("pressure:sensor/fluid")
}
