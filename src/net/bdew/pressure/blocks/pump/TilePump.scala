/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.pump

import net.bdew.lib.capabilities.helpers.FluidHelper
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.data.base.TileDataSlotsTicking
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.misc.FakeFluidHandler
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack

class TilePump extends TileDataSlotsTicking with TileFilterable with CapabilityProvider {
  override def shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState) =
    oldState.getBlock != newSate.getBlock

  def getFacing = BlockPump.getFacing(world, pos)

  serverTick.listen(doPushFluid)

  val handler = new FakeFluidHandler {
    override def canFill: Boolean = true
    override def canFillFluidType(fluidStack: FluidStack): Boolean = isFluidAllowed(fluidStack)
    override def fill(resource: FluidStack, doFill: Boolean): Int = {
      if (resource != null && isFluidAllowed(resource)) {
        FluidHelper.getFluidHandler(world, pos.offset(getFacing), getFacing.getOpposite) map { handler =>
          handler.fill(resource, doFill)
        } getOrElse 0
      } else 0
    }
  }

  addCapabilityOption(Capabilities.CAP_FLUID_HANDLER) { side =>
    if (side == getFacing.getOpposite)
      Some(handler)
    else
      None
  }

  def doPushFluid() {
    if (BlockPump.getSignal(world, pos)) {
      for {
        from <- FluidHelper.getFluidHandler(world, pos.offset(getFacing.getOpposite), getFacing)
        to <- FluidHelper.getFluidHandler(world, pos.offset(getFacing), getFacing.getOpposite)
      } {
        FluidHelper.pushFluid(from, to)
      }
    }
  }
}
