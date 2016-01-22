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

class BasePoweredBlock[T <: TileFilterable](name: String, teClass: Class[T]) extends Block(Material.iron) with BlockFilterableRotatable[T] {
  override val TEClass = teClass

  setBlockName("pressure." + name)
  setHardness(2)

  @SideOnly(Side.CLIENT)
  override def getRenderType = RotatedFilterableBlockRenderer.id

  override def getFacing(world: IBlockAccess, x: Int, y: Int, z: Int) =
    ForgeDirection.values()(world.getBlockMetadata(x, y, z) & 7)

  override def setFacing(world: World, x: Int, y: Int, z: Int, facing: ForgeDirection) =
    world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) & 8 | facing.ordinal(), 3)

  override def rotateBlock(world: World, x: Int, y: Int, z: Int, axis: ForgeDirection) = {
    val meta = world.getBlockMetadata(x, y, z)
    world.setBlockMetadataWithNotify(x, y, z, (meta & 8) | (((meta & 7) + 1) % 6), 3)
  }

  override def canConnectRedstone(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Boolean = {
    val facing = getFacing(world, x, y, z)
    side != facing.ordinal() && side != facing.getOpposite.ordinal()
  }

  override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) {
    val meta = world.getBlockMetadata(x, y, z)
    val powered = world.isBlockIndirectlyGettingPowered(x, y, z)
    if (powered && ((meta & 8) == 0))
      world.setBlockMetadataWithNotify(x, y, z, (meta & 7) | 8, 2)
    else if (!powered && ((meta & 8) == 8))
      world.setBlockMetadataWithNotify(x, y, z, meta & 7, 2)
  }

  @SideOnly(Side.CLIENT)
  override def getIcon(meta: Int, kind: IconType.Value) = kind match {
    case IconType.BACK => backIcon
    case IconType.FRONT => frontIcon
    case IconType.SIDE if (meta & 8) == 8 => sideIconOn
    case _ => sideIconOff
  }

  var frontIcon, sideIconOff, sideIconOn, backIcon: IIcon = null

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(ir: IIconRegister) = {

    frontIcon = ir.registerIcon(Misc.iconName(Pressure.modId, name, "front"))
    backIcon = ir.registerIcon(Misc.iconName(Pressure.modId, name, "back"))
    sideIconOn = ir.registerIcon(Misc.iconName(Pressure.modId, name, "side_on"))
    sideIconOff = ir.registerIcon(Misc.iconName(Pressure.modId, name, "side_off"))
  }
}
