/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.{PropertyBool, PropertyDirection}
import net.minecraft.block.state.IBlockState
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}

class BasePoweredBlock[T <: TileFilterable](name: String, teClass: Class[T]) extends SimpleBlock(name, Material.iron) with HasTE[T] with BlockFilterableRotatable {
  override val TEClass = teClass
  override val facingProperty = PropertyDirection.create("facing")
  val stateProperty = PropertyBool.create("state")

  setHardness(2)

  override def canConnectRedstone(world: IBlockAccess, pos: BlockPos, side: EnumFacing) = {
    val facing = getFacing(world, pos)
    side != facing && side != facing.getOpposite
  }

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

  override def onNeighborBlockChange(world: World, pos: BlockPos, state: IBlockState, neighborBlock: Block) = {
    val powered = world.isBlockIndirectlyGettingPowered(pos) > 0
    if (powered != isPowered(world, pos))
      setPowered(world, pos, powered)
  }
}
