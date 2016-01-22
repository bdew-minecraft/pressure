/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.rotate.IconType
import net.bdew.pressure.Pressure
import net.bdew.pressure.render.RotatedFilterableBlockRenderer
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

class BaseIOBlock[T <: TileFilterable](name: String, teClass: Class[T]) extends Block(Material.iron) with BlockFilterableRotatable[T] {
  override val TEClass = teClass

  setBlockName("pressure." + name)
  setHardness(2)

  override def getFacing(world: IBlockAccess, x: Int, y: Int, z: Int) =
    ForgeDirection.values()(world.getBlockMetadata(x, y, z))

  override def setFacing(world: World, x: Int, y: Int, z: Int, facing: ForgeDirection) =
    world.setBlockMetadataWithNotify(x, y, z, facing.ordinal(), 3)

  override def rotateBlock(world: World, x: Int, y: Int, z: Int, axis: ForgeDirection) = {
    world.setBlockMetadataWithNotify(x, y, z, (world.getBlockMetadata(x, y, z) + 1) % 6, 3)
    true
  }

  @SideOnly(Side.CLIENT)
  override def getRenderType = RotatedFilterableBlockRenderer.id

  @SideOnly(Side.CLIENT)
  override def getIcon(meta: Int, kind: IconType.Value) = kind match {
    case IconType.BACK => backIcon
    case IconType.FRONT => frontIcon
    case _ => sideIcon
  }

  var frontIcon, sideIcon, backIcon: IIcon = null
  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(ir: IIconRegister) = {
    frontIcon = ir.registerIcon(Misc.iconName(Pressure.modId, name, "front"))
    backIcon = ir.registerIcon(Misc.iconName(Pressure.modId, name, "back"))
    sideIcon = ir.registerIcon(Misc.iconName(Pressure.modId, name, "side"))
  }
}
