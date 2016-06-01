/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.source

import net.bdew.lib.block.{BaseBlock, HasTE}
import net.bdew.pressure.blocks.BlockFilterable
import net.minecraft.block.material.Material

object BlockCreativeSource extends BaseBlock("CreativeSource", Material.IRON) with HasTE[TileCreativeSource] with BlockFilterable {
  override val TEClass = classOf[TileCreativeSource]
  setHardness(1)
}
