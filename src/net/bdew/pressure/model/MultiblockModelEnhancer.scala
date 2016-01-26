/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.model

import net.bdew.lib.render.connected.ConnectedModelEnhancer
import net.minecraft.util.ResourceLocation

object MultiblockModelEnhancer extends ConnectedModelEnhancer(new ResourceLocation("pressure:blocks/connected/edge")) {
  lazy val withFilter = compose(FluidFilterModelEnhancer)
}
