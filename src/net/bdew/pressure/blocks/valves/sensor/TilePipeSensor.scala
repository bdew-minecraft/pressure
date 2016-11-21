/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves.sensor

import net.bdew.lib.computers.TileCommandHandler
import net.bdew.lib.data.base.{TileDataSlotsTicking, UpdateKind}
import net.bdew.pressure.api.{IPressureConnection, IPressureEject, IPressureInject}
import net.bdew.pressure.misc.{DataSlotFluidAverages, DataSlotFluidCounts, FluidMapHelpers}
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.{Fluid, FluidStack}

class TilePipeSensor extends TileDataSlotsTicking with IPressureEject with IPressureInject {
  var connection: IPressureConnection = null

  var flowThisTick = Map.empty[Fluid, Double]
  val averages = DataSlotFluidAverages("flow", this, 50).setUpdate(UpdateKind.SAVE)
  val fluidCounts = DataSlotFluidCounts("fluidCounts", this).setUpdate(UpdateKind.SAVE)

  var flowTicks = 10L
  var coolDown = 0L

  serverTick.listen { () =>
    coolDown -= 1
    flowTicks += 1
    if (coolDown <= 0) {
      val state = flowTicks < 10
      if (BlockPipeSensor.getSignal(world, pos) != state)
        BlockPipeSensor.setSignal(world, pos, state)
      coolDown = 10
    }
    averages.update(flowThisTick)
    flowThisTick = Map.empty
  }

  override def shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState) =
    oldState.getBlock != newSate.getBlock

  def getFacing = BlockPipeSensor.getFacing(world, pos)

  override def eject(resource: FluidStack, face: EnumFacing, doEject: Boolean): Int = {
    if (face == getFacing.getOpposite) {
      if (connection == null)
        connection = Helper.recalculateConnectionInfo(this, getFacing)
      val res = connection.pushFluid(resource, doEject)
      if (res > 0) {
        flowTicks = 0
        if (doEject) {
          flowThisTick += resource.getFluid -> (flowThisTick.getOrElse(resource.getFluid, 0D) + res)
          fluidCounts.update(resource.getFluid, res)
        }
      }
      res
    } else 0
  }

  override def invalidateConnection(direction: EnumFacing) = connection = null

  override def pressureNodePos = getPos
  override def pressureNodeWorld = getWorld
}

object PipeSensorCommands extends TileCommandHandler[TilePipeSensor] {
  command("getAverages", direct = true) { ctx =>
    FluidMapHelpers.fluidPairsToResult(ctx.tile.averages.getAverages, "average")
  }

  command("getCounts", direct = true) { ctx =>
    FluidMapHelpers.fluidPairsToResult(ctx.tile.fluidCounts.values, "count")
  }
}
