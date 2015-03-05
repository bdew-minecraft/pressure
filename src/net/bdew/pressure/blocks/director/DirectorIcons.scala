/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director

import net.bdew.lib.render.IconPreloader
import net.bdew.pressure.blocks.director.data.DirectorSideMode

object DirectorIcons extends IconPreloader(0) {
  val overlays = Map(
    DirectorSideMode.DISABLED -> TextureLoc("pressure:director/closed_over"),
    DirectorSideMode.INPUT -> TextureLoc("pressure:director/input_over"),
    DirectorSideMode.OUTPUT_HIGH -> TextureLoc("pressure:director/output_over_high"),
    DirectorSideMode.OUTPUT_MEDIUM -> TextureLoc("pressure:director/output_over_med"),
    DirectorSideMode.OUTPUT_LOW -> TextureLoc("pressure:director/output_over_low")
  )

  val modeIcons = Map(
    DirectorSideMode.DISABLED -> TextureLoc("pressure:director/closed"),
    DirectorSideMode.INPUT -> TextureLoc("pressure:director/in"),
    DirectorSideMode.OUTPUT_HIGH -> TextureLoc("pressure:director/out_high"),
    DirectorSideMode.OUTPUT_MEDIUM -> TextureLoc("pressure:director/out_med"),
    DirectorSideMode.OUTPUT_LOW -> TextureLoc("pressure:director/out_low")
  )
}
