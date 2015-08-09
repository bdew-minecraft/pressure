/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.block.NamedBlock
import net.bdew.lib.multiblock.block.BlockModule
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.config.Machines
import net.bdew.pressure.{Pressure, PressureResourceProvider}
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister

class BaseModule[T <: TileModule](name: String, kind: String, TEClass: Class[T])
  extends BlockModule(name, kind, Material.iron, TEClass, Machines) with NamedBlock {
  override def resources = PressureResourceProvider

  setBlockName("pressure." + name)
  setHardness(1)

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(ir: IIconRegister) {
    blockIcon = ir.registerIcon(Misc.iconName(Pressure.modId, "tank", name, "main"))
    regIcons(ir)
  }

  @SideOnly(Side.CLIENT)
  def regIcons(ir: IIconRegister) {}
}
