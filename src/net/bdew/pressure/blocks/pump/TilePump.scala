/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.pump

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.data.base.TileDataSlotsTicking
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.misc.FakeTank
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.{Fluid, FluidStack, IFluidHandler}

class TilePump extends TileDataSlotsTicking with FakeTank with TileFilterable {
  override def shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState) =
    oldState.getBlock != newSate.getBlock

  def getFacing = BlockPump.getFacing(worldObj, pos)

  serverTick.listen(doPushFluid)

  def doPushFluid() {
    if (!BlockPump.getSignal(worldObj, pos)) return
    val face = getFacing
    for {
      from <- worldObj.getTileSafe[IFluidHandler](pos.offset(face.getOpposite))
      to <- worldObj.getTileSafe[IFluidHandler](pos.offset(face))
    } {
      val res = from.drain(face, Int.MaxValue, false)
      if (res != null && res.getFluid != null && res.amount > 0 && isFluidAllowed(res)) {
        val filled = to.fill(face.getOpposite, res, true)
        if (filled > 0)
          from.drain(face, filled, true)
      }
    }
  }

  override def canFill(from: EnumFacing, fluid: Fluid) = from == getFacing.getOpposite && isFluidAllowed(fluid)
  override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean) =
    if (resource != null && canFill(from, resource.getFluid))
      worldObj.getTileSafe[IFluidHandler](pos.offset(getFacing)) map { target =>
        target.fill(from, resource, doFill)
      } getOrElse 0
    else 0

  override def isValidDirectionForFakeTank(dir: EnumFacing) = dir == getFacing || dir.getOpposite == getFacing
}
