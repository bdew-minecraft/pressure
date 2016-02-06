/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router

import net.bdew.lib.gui.Texture
import net.bdew.pressure.blocks.router.data.RouterSideMode
import net.minecraft.util.ResourceLocation

object RouterIcons {
  val overlays = Map(
    RouterSideMode.DISABLED -> new ResourceLocation("pressure:blocks/router/closed_over"),
    RouterSideMode.INPUT_PASSIVE -> new ResourceLocation("pressure:blocks/router/input_over"),
    RouterSideMode.INPUT_ACTIVE -> new ResourceLocation("pressure:blocks/router/input_act_over"),
    RouterSideMode.OUTPUT_HIGH -> new ResourceLocation("pressure:blocks/router/output_over_high"),
    RouterSideMode.OUTPUT_MEDIUM -> new ResourceLocation("pressure:blocks/router/output_over_med"),
    RouterSideMode.OUTPUT_LOW -> new ResourceLocation("pressure:blocks/router/output_over_low")
  )

  val modeIcons = Map(
    RouterSideMode.DISABLED -> Texture("pressure:textures/gui/router/closed.png"),
    RouterSideMode.INPUT_PASSIVE -> Texture("pressure:textures/gui/router/in.png"),
    RouterSideMode.INPUT_ACTIVE -> Texture("pressure:textures/gui/router/in_act.png"),
    RouterSideMode.OUTPUT_HIGH -> Texture("pressure:textures/gui/router/out_high.png"),
    RouterSideMode.OUTPUT_MEDIUM -> Texture("pressure:textures/gui/router/out_med.png"),
    RouterSideMode.OUTPUT_LOW -> Texture("pressure:textures/gui/router/out_low.png")
  )
}
