/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.pipe

import net.bdew.lib.block.{BaseBlock, HasItemBlock}
import net.bdew.lib.property.EnumerationProperty
import net.bdew.pressure.api.IPressureConnectableBlock
import net.bdew.pressure.blocks.{BlockNotifyUpdates, CustomItemBlock}
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.IBlockState
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}

object BlockPipe extends BaseBlock("pipe", Material.iron) with IPressureConnectableBlock with BlockNotifyUpdates with HasItemBlock {
  override val ItemBlockClass = classOf[CustomItemBlock]
  setHardness(2)

  object Straight extends Enumeration {
    val x, y, z, none = Value
  }

  object Properties {
    val CONNECTED = (EnumFacing.values() map (dir => dir -> PropertyBool.create(dir.toString))).toMap
    val STRAIGHT = EnumerationProperty.create(Straight, "straight")
  }

  setDefaultState(
    EnumFacing.values().foldLeft(getDefaultState.withProperty(Properties.STRAIGHT, Straight.none)) { (state, face) =>
      state.withProperty(Properties.CONNECTED(face), Boolean.box(true))
    }
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
      connections.foldLeft(state.withProperty(Properties.STRAIGHT, Straight.none)) { (state, face) =>
        state.withProperty(Properties.CONNECTED(face), Boolean.box(true))
      }
    }
  }

  override def getMetaFromState(state: IBlockState) = 0

  override def isOpaqueCube = false
  override def isFullCube = false

  override def setBlockBoundsBasedOnState(w: IBlockAccess, pos: BlockPos) {
    val connections = Helper.getPipeConnections(w, pos)
    val minX = if (connections.contains(EnumFacing.WEST)) 0 else 0.2F
    val maxX = if (connections.contains(EnumFacing.EAST)) 1 else 0.8F

    val minY = if (connections.contains(EnumFacing.DOWN)) 0 else 0.2F
    val maxY = if (connections.contains(EnumFacing.UP)) 1 else 0.8F

    val minZ = if (connections.contains(EnumFacing.NORTH)) 0 else 0.2F
    val maxZ = if (connections.contains(EnumFacing.SOUTH)) 1 else 0.8F

    this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ)
  }

  override def getCollisionBoundingBox(w: World, pos: BlockPos, state: IBlockState) = {
    setBlockBoundsBasedOnState(w, pos)
    super.getCollisionBoundingBox(w, pos, state)
  }

  override def setBlockBoundsForItemRender() {
    this.setBlockBounds(0, 0, 0, 1, 1, 1)
  }

  override def canConnectTo(world: IBlockAccess, pos: BlockPos, side: EnumFacing) = true
  override def isTraversable(world: IBlockAccess, pos: BlockPos) = true
}
