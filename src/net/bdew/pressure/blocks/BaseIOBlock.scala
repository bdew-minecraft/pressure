/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import net.bdew.lib.block.{BaseBlock, HasTE}
import net.bdew.lib.rotate.BlockFacingMeta
import net.minecraft.block.material.Material

class BaseIOBlock[T <: TileFilterable](name: String, teClass: Class[T]) extends BaseBlock(name, Material.IRON) with HasTE[T] with BlockFilterableRotatable with BlockFacingMeta {
  override val TEClass = teClass
  setHardness(2)
}
