/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.controller

import net.bdew.pressure.blocks.tank.BaseController
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BlockTankController extends BaseController("tank_controller", classOf[TileTankController]) {
  override def breakBlock(world: World, pos: BlockPos, state: IBlockState) = {
    if (!world.isRemote) {
      getTE(world, pos).dropItems()
    }
    super.breakBlock(world, pos, state)
  }
}
