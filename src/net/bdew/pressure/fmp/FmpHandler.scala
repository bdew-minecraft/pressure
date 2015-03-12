/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp

import codechicken.multipart.{MultiPartRegistry, MultipartGenerator}
import net.bdew.pressure.pressurenet.Helper

object FmpHandler {
  def init() {
    MultiPartRegistry.registerParts(FmpFactory, Array(
      "bdew.pressure.pipe",
      "bdew.pressure.checkvalve",
      "bdew.pressure.pipesensor"
    ))
    MultiPartRegistry.registerConverter(FmpConverter)
    Helper.registerExtension(FmpPressureExtension)

    // here be dragons (and ASM)
    MultipartGenerator.registerTrait("net.bdew.pressure.fmp.traits.TInjectPart", "net.bdew.pressure.fmp.traits.TileInject")
    MultipartGenerator.registerTrait("net.bdew.pressure.fmp.traits.TEjectPart", "net.bdew.pressure.fmp.traits.TileEject")
  }
}
