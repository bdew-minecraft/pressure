/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.block.SimpleBlock
import net.bdew.lib.rotate.{BaseRotatableBlock, IconType}
import net.bdew.pressure.api.IPressureConnectableBlock
import net.bdew.pressure.blocks.BlockNotifyUpdates
import net.bdew.pressure.render.ValveRenderer
import net.minecraft.block.material.Material
import net.minecraft.util.IIcon
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

class BlockValve(name: String) extends SimpleBlock(name, Material.iron) with BaseRotatableBlock with IPressureConnectableBlock with BlockNotifyUpdates {
  // ==== BLOCK SETTINGS ====

  override def getRenderType = ValveRenderer.id
  override def renderAsNormalBlock() = false
  override def isOpaqueCube = false
  override def getDefaultFacing: ForgeDirection = ForgeDirection.NORTH

  // ==== PRESSURE NET ====

  override def canConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) = {
    val facing = getFacing(world, x, y, z)
    facing == side || facing == side.getOpposite
  }

  override def isTraversable(world: IBlockAccess, x: Int, y: Int, z: Int) = false

  // ==== BOUNDS ====

  val boundsFromFacing = Map(
    ForgeDirection.UP ->(0.2F, 0.125F, 0.2F, 0.8F, 0.875F, 0.8F),
    ForgeDirection.DOWN ->(0.2F, 0.125F, 0.2F, 0.8F, 0.875F, 0.8F),
    ForgeDirection.NORTH ->(0.2F, 0.2F, 0.125F, 0.8F, 0.8F, 0.875F),
    ForgeDirection.SOUTH ->(0.2F, 0.2F, 0.125F, 0.8F, 0.8F, 0.875F),
    ForgeDirection.EAST ->(0.125F, 0.2F, 0.2F, 0.875F, 0.8F, 0.8F),
    ForgeDirection.WEST ->(0.125F, 0.2F, 0.2F, 0.875F, 0.8F, 0.8F)
  )

  val setBlockBoundsTupled = (setBlockBounds _).tupled

  override def setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int): Unit =
    setBlockBoundsTupled(boundsFromFacing(getFacing(world, x, y, z)))

  override def getCollisionBoundingBoxFromPool(w: World, x: Int, y: Int, z: Int) = {
    setBlockBoundsBasedOnState(w, x, y, z)
    super.getCollisionBoundingBoxFromPool(w, x, y, z)
  }

  override def setBlockBoundsForItemRender(): Unit = {
    setBlockBoundsTupled(boundsFromFacing(getDefaultFacing))
  }

  // ==== ICONS ====

  var frontIcon, sideIconOff, sideIconOn: IIcon = null

  @SideOnly(Side.CLIENT)
  override def getIcon(meta: Int, kind: IconType.Value) = kind match {
    case IconType.BACK => frontIcon
    case IconType.FRONT => frontIcon
    case IconType.SIDE if (meta & 8) == 8 => sideIconOn
    case _ => sideIconOff
  }

  // ==== METADATA ====

  val powerStates = Map(true -> 8, false -> 0)

  def isPowered(world: IBlockAccess, x: Int, y: Int, z: Int) =
    (world.getBlockMetadata(x, y, z) & 8) == 8

  def setPowered(world: World, x: Int, y: Int, z: Int, isPowered: Boolean) =
    world.setBlockMetadataWithNotify(x, y, z, (world.getBlockMetadata(x, y, z) & 7) | powerStates(isPowered), 3)

  override def getFacing(world: IBlockAccess, x: Int, y: Int, z: Int) =
    ForgeDirection.values()(world.getBlockMetadata(x, y, z) & 7)

  override def setFacing(world: World, x: Int, y: Int, z: Int, facing: ForgeDirection) =
    world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) & 8 | facing.ordinal(), 3)

  override def rotateBlock(world: World, x: Int, y: Int, z: Int, axis: ForgeDirection) = {
    val meta = world.getBlockMetadata(x, y, z)
    world.setBlockMetadataWithNotify(x, y, z, (meta & 8) | (((meta & 7) + 1) % 6), 3)
  }
}
