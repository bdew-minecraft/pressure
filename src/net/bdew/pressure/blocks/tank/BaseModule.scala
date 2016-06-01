/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import net.bdew.lib.multiblock.block.BlockModule
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.PressureResourceProvider
import net.bdew.pressure.config.Machines
import net.minecraft.block.material.Material

class BaseModule[T <: TileModule](name: String, kind: String, TEClass: Class[T]) extends BlockModule(name, kind, Material.IRON, TEClass, Machines) {
  override def resources = PressureResourceProvider
  setHardness(1)
}
