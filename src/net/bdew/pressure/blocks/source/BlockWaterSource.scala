/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.source

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.block.HasTE
import net.bdew.pressure.Pressure
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister

object BlockWaterSource extends Block(Material.iron) with HasTE[TileWaterSource] {
  override val TEClass = classOf[TileWaterSource]
  setBlockName("pressure.water")
  setHardness(1)

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(ir: IIconRegister) = {
    blockIcon = ir.registerIcon(Misc.iconName(Pressure.modId, "water"))
  }
}
