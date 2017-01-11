/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import net.bdew.lib.block.{BaseBlock, HasTE}
import net.bdew.lib.rotate.BlockFacingSignalMeta
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{IBlockAccess, World}

class BasePoweredBlock[T <: TileFilterable](name: String, teClass: Class[T]) extends BaseBlock(name, Material.IRON) with HasTE[T] with BlockFilterableRotatable with BlockFacingSignalMeta {
  override val TEClass = teClass
  lazy val POWERED = PropertyBool.create("powered")

  setHardness(2)

  override def canConnectRedstone(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean = {
    val facing = getFacing(world, pos)
    side != facing && side != facing.getOpposite
  }

  override def neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos): Unit = {
    val powered = world.isBlockIndirectlyGettingPowered(pos) > 0
    if (powered != getSignal(state))
      setSignal(world, pos, powered)
  }
}
