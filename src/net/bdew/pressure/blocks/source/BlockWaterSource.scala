/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.blocks.source

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.bdew.lib.block.HasTE
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.renderer.texture.IconRegister

class BlockWaterSource(id: Int) extends Block(id, Material.iron) with HasTE[TileWaterSource] {
  override val TEClass = classOf[TileWaterSource]
  setUnlocalizedName("pressure.water")
  setHardness(1)

  @SideOnly(Side.CLIENT)
  override def registerIcons(ir: IconRegister) = {
    blockIcon = ir.registerIcon("pressure:water")
  }
}
