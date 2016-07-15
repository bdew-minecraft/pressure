/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.multiblock.block.BlockOutput
import net.bdew.lib.multiblock.data.OutputConfigFluid
import net.bdew.pressure.blocks.tank.{BaseModule, TileFluidOutputBase}
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

object BlockFluidOutput extends BaseModule("TankFluidOutput", "FluidOutput", classOf[TileFluidOutput]) with BlockOutput[TileFluidOutput] {
  override def canConnectRedstone(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean = true
}

class TileFluidOutput extends TileFluidOutputBase {
  override def doOutput(face: EnumFacing, cfg: OutputConfigFluid) {}
}
