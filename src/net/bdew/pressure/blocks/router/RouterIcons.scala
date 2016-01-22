/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router

import net.bdew.lib.render.IconPreloader
import net.bdew.pressure.blocks.router.data.RouterSideMode

object RouterIcons extends IconPreloader(0) {
  val overlays = Map(
    RouterSideMode.DISABLED -> TextureLoc("pressure:router/closed_over"),
    RouterSideMode.INPUT_PASSIVE -> TextureLoc("pressure:router/input_over"),
    RouterSideMode.INPUT_ACTIVE -> TextureLoc("pressure:router/input_act_over"),
    RouterSideMode.OUTPUT_HIGH -> TextureLoc("pressure:router/output_over_high"),
    RouterSideMode.OUTPUT_MEDIUM -> TextureLoc("pressure:router/output_over_med"),
    RouterSideMode.OUTPUT_LOW -> TextureLoc("pressure:router/output_over_low")
  )

  val modeIcons = Map(
    RouterSideMode.DISABLED -> TextureLoc("pressure:router/closed"),
    RouterSideMode.INPUT_PASSIVE -> TextureLoc("pressure:router/in"),
    RouterSideMode.INPUT_ACTIVE -> TextureLoc("pressure:router/in_act"),
    RouterSideMode.OUTPUT_HIGH -> TextureLoc("pressure:router/out_high"),
    RouterSideMode.OUTPUT_MEDIUM -> TextureLoc("pressure:router/out_med"),
    RouterSideMode.OUTPUT_LOW -> TextureLoc("pressure:router/out_low")
  )
}
