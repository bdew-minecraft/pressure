/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.checkvalve

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.bdew.lib.render.RotatedBlockRenderer
import net.bdew.lib.rotate.{BaseRotatableBlock, IconType}
import net.bdew.pressure.api.IPressureConnectableBlock
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

object BlockCheckValve extends SimpleBlock("CheckValve", Material.iron) with HasTE[TileCheckValve] with BaseRotatableBlock with IPressureConnectableBlock {
  override val TEClass = classOf[TileCheckValve]

  setHardness(2)

  override def getRenderType = RotatedBlockRenderer.id

  override def canConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) = {
    val facing = getFacing(world, x, y, z)
    facing == side || facing == side.getOpposite
  }

  override def isTraversable(world: IBlockAccess, x: Int, y: Int, z: Int) = false

  override def getDefaultFacing: ForgeDirection = ForgeDirection.NORTH

  override def setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int): Unit = {
    getFacing(world, x, y, z) match {
      case ForgeDirection.UP => setBlockBounds(0.2F, 0, 0.2F, 0.8F, 1F, 0.8F)
      case ForgeDirection.DOWN => setBlockBounds(0.2F, 0, 0.2F, 0.8F, 1F, 0.8F)
      case ForgeDirection.NORTH => setBlockBounds(0.2F, 0.2F, 0F, 0.8F, 0.8F, 1F)
      case ForgeDirection.SOUTH => setBlockBounds(0.2F, 0.2F, 0F, 0.8F, 0.8F, 1F)
      case ForgeDirection.EAST => setBlockBounds(0F, 0.2F, 0.2F, 1F, 0.8F, 0.8F)
      case ForgeDirection.WEST => setBlockBounds(0F, 0.2F, 0.2F, 1F, 0.8F, 0.8F)
      case _ =>
    }
  }

  override def renderAsNormalBlock() = false
  override def isOpaqueCube = false

  override def getCollisionBoundingBoxFromPool(w: World, x: Int, y: Int, z: Int) = {
    setBlockBoundsBasedOnState(w, x, y, z)
    super.getCollisionBoundingBoxFromPool(w, x, y, z)
  }

  override def setBlockBoundsForItemRender(): Unit = {
    setBlockBounds(0.2F, 0.2F, 0F, 0.8F, 0.8F, 1F)
  }

  override def getFacing(world: IBlockAccess, x: Int, y: Int, z: Int) =
    ForgeDirection.values()(world.getBlockMetadata(x, y, z) & 7)

  override def setFacing(world: World, x: Int, y: Int, z: Int, facing: ForgeDirection) =
    world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) & 8 | facing.ordinal(), 3)

  override def rotateBlock(world: World, x: Int, y: Int, z: Int, axis: ForgeDirection) = {
    val meta = world.getBlockMetadata(x, y, z)
    world.setBlockMetadataWithNotify(x, y, z, (meta & 8) | (((meta & 7) + 1) % 6), 3)
  }

  override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) {
    val meta = world.getBlockMetadata(x, y, z)
    val powered = world.isBlockIndirectlyGettingPowered(x, y, z)
    if (powered && ((meta & 8) == 0))
      world.setBlockMetadataWithNotify(x, y, z, (meta & 7) | 8, 2)
    else if (!powered && ((meta & 8) == 8))
      world.setBlockMetadataWithNotify(x, y, z, meta & 7, 2)
  }

  override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, ent: EntityLivingBase, stack: ItemStack): Unit = {
    super.onBlockPlacedBy(world, x, y, z, ent, stack)
    onNeighborBlockChange(world, x, y, z, this)
  }

  @SideOnly(Side.CLIENT)
  override def getIcon(meta: Int, kind: IconType.Value) = kind match {
    case IconType.BACK => frontIcon
    case IconType.FRONT => frontIcon
    case IconType.SIDE if (meta & 8) == 8 => sideIconOn
    case _ => sideIconOff
  }

  var frontIcon, sideIconOff, sideIconOn: IIcon = null

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(ir: IIconRegister) = {
    frontIcon = ir.registerIcon("pressure:%s/front".format(name.toLowerCase))
    sideIconOn = ir.registerIcon("pressure:%s/side_on".format(name.toLowerCase))
    sideIconOff = ir.registerIcon("pressure:%s/side_off".format(name.toLowerCase))
  }
}
