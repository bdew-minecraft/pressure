/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.source

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.pressure.Pressure
import net.bdew.pressure.blocks.BlockFilterable
import net.bdew.pressure.render.FilterableBlockRenderer
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection

object BlockCreativeSource extends Block(Material.iron) with BlockFilterable[TileCreativeSource] {
  override val TEClass = classOf[TileCreativeSource]
  setBlockName("pressure.creative")
  setHardness(1)

  override def shouldShowFilterIconOnSide(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) = true

  override def getRenderType: Int = FilterableBlockRenderer.id

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(ir: IIconRegister) = {
    blockIcon = ir.registerIcon(Misc.iconName(Pressure.modId, "creative"))
  }
}
