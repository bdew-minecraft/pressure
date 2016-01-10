/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection

class BaseIOBlock[T <: TileFilterable](name: String, teClass: Class[T]) extends SimpleBlock(name, Material.iron) with HasTE[T] with BlockFilterableRotatable {
  override val facingProperty = PropertyDirection.create("facing")
  override val TEClass = teClass

  setHardness(2)
}
