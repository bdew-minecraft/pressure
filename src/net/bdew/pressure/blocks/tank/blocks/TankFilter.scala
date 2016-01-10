/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.blocks.tank.{BaseModule, MIFilterable, ModuleNeedsRenderUpdate}
import net.bdew.pressure.items.configurator.ItemConfigurator
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

object BlockTankFilter extends BaseModule("TankFilter", "FluidFilter", classOf[TileTankFilter]) with ModuleNeedsRenderUpdate {
  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) = {
    if (player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem == ItemConfigurator)
      false // Let the configurator handle the click
    else
      super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ)
  }
}

class TileTankFilter extends TileModule with MIFilterable {
  val kind: String = "FluidFilter"
}