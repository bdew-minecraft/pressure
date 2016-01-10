/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.block.SimpleBlock
import net.bdew.lib.rotate.BaseRotatableBlock
import net.bdew.pressure.api.IPressureConnectableBlock
import net.bdew.pressure.blocks.BlockNotifyUpdates
import net.minecraft.block.material.Material
import net.minecraft.block.properties.{PropertyBool, PropertyDirection}
import net.minecraft.block.state.IBlockState
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}

class BlockValve(name: String) extends SimpleBlock(name, Material.iron) with BaseRotatableBlock with IPressureConnectableBlock with BlockNotifyUpdates {
  override val facingProperty = PropertyDirection.create("facing")
  val stateProperty = PropertyBool.create("state")

  // ==== BLOCK SETTINGS ====

  override def isOpaqueCube = false
  override def getDefaultFacing: EnumFacing = EnumFacing.NORTH

  // ==== PRESSURE NET ====

  override def canConnectTo(world: IBlockAccess, pos: BlockPos, side: EnumFacing) = {
    val facing = getFacing(world, pos)
    facing == side || facing == side.getOpposite
  }

  override def isTraversable(world: IBlockAccess, pos: BlockPos) = false

  // ==== BOUNDS ====

  val boundsFromFacing = Map(
    EnumFacing.UP ->(0.2F, 0.125F, 0.2F, 0.8F, 0.875F, 0.8F),
    EnumFacing.DOWN ->(0.2F, 0.125F, 0.2F, 0.8F, 0.875F, 0.8F),
    EnumFacing.NORTH ->(0.2F, 0.2F, 0.125F, 0.8F, 0.8F, 0.875F),
    EnumFacing.SOUTH ->(0.2F, 0.2F, 0.125F, 0.8F, 0.8F, 0.875F),
    EnumFacing.EAST ->(0.125F, 0.2F, 0.2F, 0.875F, 0.8F, 0.8F),
    EnumFacing.WEST ->(0.125F, 0.2F, 0.2F, 0.875F, 0.8F, 0.8F)
  )

  val setBlockBoundsTupled = (setBlockBounds _).tupled

  override def setBlockBoundsBasedOnState(world: IBlockAccess, pos: BlockPos): Unit =
    setBlockBoundsTupled(boundsFromFacing(getFacing(world, pos)))

  override def getCollisionBoundingBox(w: World, pos: BlockPos, state: IBlockState) = {
    setBlockBoundsBasedOnState(w, pos)
    super.getCollisionBoundingBox(w, pos, state)
  }

  override def setBlockBoundsForItemRender(): Unit = {
    setBlockBoundsTupled(boundsFromFacing(getDefaultFacing))
  }

  // ==== METADATA ====

  override def getStateFromMeta(meta: Int) =
    getDefaultState
      .withProperty(facingProperty, EnumFacing.getFront(meta & 7))
      .withProperty(stateProperty, Boolean.box((meta & 8) > 0))

  override def getMetaFromState(state: IBlockState) = {
    state.getValue(facingProperty).ordinal() | (if (state.getValue(stateProperty)) 8 else 0)
  }

  def isPowered(world: IBlockAccess, pos: BlockPos) =
    world.getBlockState(pos).getValue(stateProperty)

  def setPowered(world: World, pos: BlockPos, signal: Boolean): Unit = {
    world.changeBlockState(pos, 3) { state =>
      state.withProperty(stateProperty, Boolean.box(signal))
    }
  }
}
