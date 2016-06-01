/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves

import net.bdew.lib.block.BaseBlock
import net.bdew.lib.rotate.{BaseRotatableBlock, BlockFacingSignalMeta}
import net.bdew.pressure.api.IPressureConnectableBlock
import net.bdew.pressure.blocks.BlockNotifyUpdates
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.IBlockAccess

class BlockValve(name: String) extends BaseBlock(name, Material.IRON) with BaseRotatableBlock with IPressureConnectableBlock with BlockNotifyUpdates with BlockFacingSignalMeta {
  // ==== BLOCK SETTINGS ====

  override def isOpaqueCube(state: IBlockState) = false
  override def isFullCube(state: IBlockState) = false
  override def getDefaultFacing: EnumFacing = EnumFacing.NORTH

  object Properties {
    val FRONT = PropertyBool.create("front")
    val BACK = PropertyBool.create("back")
  }

  // ==== BLOCKSTATE ====

  override def getProperties = super.getProperties :+ Properties.FRONT :+ Properties.BACK

  setDefaultState(getDefaultState
    .withProperty(Properties.FRONT, Boolean.box(false))
    .withProperty(Properties.BACK, Boolean.box(false))
  )

  override def getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos) = {
    val facing = getFacing(world, pos)
    state
      .withProperty(Properties.FRONT, Boolean.box(Helper.canPipeConnectTo(world, pos.offset(facing), facing.getOpposite)))
      .withProperty(Properties.BACK, Boolean.box(Helper.canPipeConnectTo(world, pos.offset(facing.getOpposite), facing)))
  }

  // ==== PRESSURE NET ====

  override def canConnectTo(world: IBlockAccess, pos: BlockPos, side: EnumFacing) = {
    val facing = getFacing(world, pos)
    facing == side || facing == side.getOpposite
  }

  override def isTraversable(world: IBlockAccess, pos: BlockPos) = false

  // ==== BOUNDS ====

  val boundsFromFacing = Map(
    EnumFacing.UP -> new AxisAlignedBB(0.2F, 0.125F, 0.2F, 0.8F, 0.875F, 0.8F),
    EnumFacing.DOWN -> new AxisAlignedBB(0.2F, 0.125F, 0.2F, 0.8F, 0.875F, 0.8F),
    EnumFacing.NORTH -> new AxisAlignedBB(0.2F, 0.2F, 0.125F, 0.8F, 0.8F, 0.875F),
    EnumFacing.SOUTH -> new AxisAlignedBB(0.2F, 0.2F, 0.125F, 0.8F, 0.8F, 0.875F),
    EnumFacing.EAST -> new AxisAlignedBB(0.125F, 0.2F, 0.2F, 0.875F, 0.8F, 0.8F),
    EnumFacing.WEST -> new AxisAlignedBB(0.125F, 0.2F, 0.2F, 0.875F, 0.8F, 0.8F)
  )

  override def getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB =
    boundsFromFacing(getFacing(state))
}
