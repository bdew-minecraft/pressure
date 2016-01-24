/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves.sensor

import net.bdew.lib.block.BlockRef
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.pressure.api.{IPressureConnection, IPressureEject, IPressureInject}
import net.bdew.pressure.compat.computers.TileCommandHandler
import net.bdew.pressure.misc.{FluidMapHelpers, DataSlotFluidCounts, DataSlotFluidAverages}
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.block.Block
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{Fluid, FluidStack}

class TilePipeSensor extends TileDataSlots with IPressureEject with IPressureInject {
  var connection: IPressureConnection = null
  lazy val me = BlockRef.fromTile(this)

  var flowThisTick = Map.empty[Fluid, Double]
  val averages = DataSlotFluidAverages("flow", this, 50).setUpdate(UpdateKind.SAVE)
  val fluidCounts = DataSlotFluidCounts("fluidCounts", this, 50).setUpdate(UpdateKind.SAVE)

  var flowTicks = 10L
  var coolDown = 0L

  serverTick.listen { () =>
    coolDown -= 1
    flowTicks += 1
    if (coolDown <= 0) {
      val state = flowTicks < 10
      if (BlockPipeSensor.isPowered(worldObj, xCoord, yCoord, zCoord) != state)
        BlockPipeSensor.setPowered(worldObj, xCoord, yCoord, zCoord, state)
      coolDown = 10
    }
    averages.update(flowThisTick)
    flowThisTick = Map.empty
  }

  override def shouldRefresh(oldBlock: Block, newBlock: Block, oldMeta: Int, newMeta: Int, world: World, x: Int, y: Int, z: Int) =
    oldBlock != newBlock

  def getFacing = BlockPipeSensor.getFacing(worldObj, xCoord, yCoord, zCoord)

  override def eject(resource: FluidStack, face: ForgeDirection, doEject: Boolean): Int = {
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

  override def invalidateConnection(direction: ForgeDirection) = connection = null

  override def getZCoord = zCoord
  override def getYCoord = yCoord
  override def getXCoord = xCoord
  override def getWorld = worldObj
}

object PipeSensorCommands extends TileCommandHandler[TilePipeSensor] {
  command("getAverages", direct = true) { ctx =>
    FluidMapHelpers.fluidPairsToResult(ctx.tile.averages.getAverages.iterator, "average")
  }

  command("getCounts", direct = true) { ctx =>
    FluidMapHelpers.fluidPairsToResult(ctx.tile.fluidCounts.values.iterator, "count")
  }
}
