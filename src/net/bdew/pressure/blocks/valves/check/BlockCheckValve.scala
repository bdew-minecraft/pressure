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
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BlockCheckValve extends BlockValve("check_valve") with HasTE[TileCheckValve] with HasItemBlock {
  override val TEClass = classOf[TileCheckValve]
  override val itemBlockInstance: ItemBlock = new CustomItemBlock(this)

  setHardness(2)

  override def neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos): Unit = {
    val powered = world.isBlockIndirectlyGettingPowered(pos) > 0
    if (powered != getSignal(world, pos))
      setSignal(world, pos, powered)
  }

  override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) = {
    super.onBlockPlacedBy(world, pos, state, placer, stack)
    neighborChanged(state, world, pos, this, pos)
  }
}
