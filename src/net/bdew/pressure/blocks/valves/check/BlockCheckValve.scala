/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves.check

import net.bdew.lib.block.{HasItemBlock, HasTE}
import net.bdew.pressure.blocks.CustomItemBlock
import net.bdew.pressure.blocks.valves.BlockValve
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.world.World

object BlockCheckValve extends BlockValve("CheckValve") with HasTE[TileCheckValve] with HasItemBlock {
  override val TEClass = classOf[TileCheckValve]
  override val ItemBlockClass = classOf[CustomItemBlock]

  setHardness(2)

  override def onNeighborBlockChange(world: World, pos: BlockPos, state: IBlockState, neighborBlock: Block) = {
    val powered = world.isBlockIndirectlyGettingPowered(pos) > 0
    if (powered != getSignal(world, pos))
      setSignal(world, pos, powered)
  }

  override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) = {
    super.onBlockPlacedBy(world, pos, state, placer, stack)
    onNeighborBlockChange(world, pos, state, this)
  }
}
