/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.pipe

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.block.{BaseBlock, HasItemBlock}
import net.bdew.lib.property.EnumerationProperty
import net.bdew.pressure.api.IPressureConnectableBlock
import net.bdew.pressure.blocks.{BlockNotifyUpdates, CustomItemBlock}
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.IBlockAccess

object BlockPipe extends BaseBlock("pipe", Material.IRON) with IPressureConnectableBlock with BlockNotifyUpdates with HasItemBlock {
  override val itemBlockInstance: ItemBlock = new CustomItemBlock(this)
  setHardness(2)

  object Straight extends Enumeration {
    val x, y, z, none = Value
  }

  object Properties {
    val CONNECTED = (EnumFacing.values() map (dir => dir -> PropertyBool.create(dir.toString))).toMap
    val STRAIGHT = EnumerationProperty.create(Straight, "straight")
  }

  setDefaultState(
    getDefaultState
      .withProperty(Properties.STRAIGHT, Straight.none)
      .withProperties(EnumFacing.values().map(f => Properties.CONNECTED(f) -> Boolean.box(false)))
  )

  override def getProperties =
    super.getProperties ++ Properties.CONNECTED.values :+ Properties.STRAIGHT

  override def getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos) = {
    val connections = Helper.getPipeConnections(world, pos).toSet
    if (connections == Set(EnumFacing.EAST, EnumFacing.WEST))
      state.withProperty(Properties.STRAIGHT, Straight.x)
    else if (connections == Set(EnumFacing.NORTH, EnumFacing.SOUTH))
      state.withProperty(Properties.STRAIGHT, Straight.z)
    else if (connections == Set(EnumFacing.UP, EnumFacing.DOWN))
      state.withProperty(Properties.STRAIGHT, Straight.y)
    else {
      state
        .withProperty(Properties.STRAIGHT, Straight.none)
        .withProperties(connections.map(f => Properties.CONNECTED(f) -> Boolean.box(true)))
    }
  }

  override def getMetaFromState(state: IBlockState) = 0

  override def isOpaqueCube(state: IBlockState) = false
  override def isFullCube(state: IBlockState) = false

  override def getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB = {
    val connections = Helper.getPipeConnections(source, pos)
    val minX = if (connections.contains(EnumFacing.WEST)) 0 else 0.2F
    val maxX = if (connections.contains(EnumFacing.EAST)) 1 else 0.8F

    val minY = if (connections.contains(EnumFacing.DOWN)) 0 else 0.2F
    val maxY = if (connections.contains(EnumFacing.UP)) 1 else 0.8F

    val minZ = if (connections.contains(EnumFacing.NORTH)) 0 else 0.2F
    val maxZ = if (connections.contains(EnumFacing.SOUTH)) 1 else 0.8F

    new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
  }

  override def canConnectTo(world: IBlockAccess, pos: BlockPos, side: EnumFacing) = true
  override def isTraversable(world: IBlockAccess, pos: BlockPos) = true
}
