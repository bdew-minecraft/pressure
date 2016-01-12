/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.source

import net.bdew.lib.block.{BaseBlock, HasTE}
import net.minecraft.block.material.Material

object BlockWaterSource extends BaseBlock("WaterSource", Material.iron) with HasTE[TileWaterSource] {
  override val TEClass = classOf[TileWaterSource]
  setHardness(1)
}
