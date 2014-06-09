/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.misc

import net.bdew.pressure.api._
import net.minecraftforge.common.ForgeDirection
import net.minecraftforge.fluids.FluidStack
import net.minecraft.world.{IBlockAccess, World}
import net.minecraft.block.Block
import net.bdew.pressure.blocks.BlockPipe
import net.bdew.pressure.Pressure

object Helper extends IPressureHelper {
  val recursionGuard = new ThreadLocal[Set[ConnectionInfo]] {
    override def initialValue() = Set.empty
  }

  def scanConnectedBlocks(start: BlockRef, forceNeighbours: Boolean) = {
    val seen = collection.mutable.Set.empty[BlockRef]
    val queue = collection.mutable.Queue(start)

    if (forceNeighbours)
      queue.enqueue(start.neighbours map (_._2): _*)

    val inputs = collection.mutable.Set.empty[IPressureInject]
    val outputs = collection.mutable.Set.empty[IPressureEject]

    while (!queue.isEmpty) {
      val current = queue.dequeue()
      seen.add(current)
      if (current.getBlock[BlockPipe].isDefined)
        queue.enqueue(getPipeConnections(current) map current.neighbour filterNot seen.contains: _*)
      else current.tile collect {
        case t: IPressureInject =>
          inputs.add(t)
          queue.enqueue(ForgeDirection.VALID_DIRECTIONS
            filter (dir =>
            current.getBlock[IPressureConnectableBlock] exists (
              _.canConnectFrom(current.world.get, current.x, current.y, current.z, dir)))
            map current.neighbour
            filterNot seen.contains: _*)
        case t: IPressureEject =>
          outputs.add(t)
          queue.enqueue(ForgeDirection.VALID_DIRECTIONS
            filter (dir =>
            current.getBlock[IPressureConnectableBlock] exists (
              _.canConnectFrom(current.world.get, current.x, current.y, current.z, dir)))
            map current.neighbour
            filterNot seen.contains: _*)
      }
    }
    (inputs.toSet, outputs.toSet, seen)
  }

  override def pushFluidIntoPressureSytem(connection: IConnectionInfo, fluid: FluidStack, doPush: Boolean): Int = {
    if (connection == null || fluid == null || fluid.getFluid == null || fluid.amount == 0 || !connection.isInstanceOf[ConnectionInfo]) return 0
    val conn = connection.asInstanceOf[ConnectionInfo]
    val recGuard = recursionGuard.get()
    if (recGuard.contains(conn)) {
      Pressure.logInfo("Detected loop, blowing up %d,%d,%d (dim %d)",
        conn.origin.getXCoord, conn.origin.getYCoord, conn.origin.getZCoord, conn.origin.getWorld.provider.dimensionId)
      conn.origin.getWorld.createExplosion(null, conn.origin.getXCoord, conn.origin.getYCoord, conn.origin.getZCoord, 1, true)
      return 0
    }
    recursionGuard.set(recGuard + conn)
    try {
      if (conn.tiles.size == 0) return 0
      if (fluid.amount < 10) {
        // Don't try balancing small amounts
        var toPush = fluid.amount
        conn.tiles.foreach { target =>
          toPush -= target.eject(new FluidStack(fluid.getFluid, toPush), doPush)
          if (toPush <= 0) return fluid.amount
        }
        toPush - fluid.amount
      } else {
        val maxFill = conn.tiles.map(target => target -> target.eject(fluid.copy(), false)).toMap
        val totalFill = maxFill.values.sum
        if (!doPush) return totalFill
        val mul = if (totalFill > fluid.amount)
          fluid.amount.toFloat / totalFill
        else
          1
        (maxFill map { case (te, amount) =>
          val toFill = (amount * mul).round
          if (toFill > 0)
            te.eject(new FluidStack(fluid.getFluid, toFill), true)
          else
            0
        }).sum
      }
    } finally {
      recursionGuard.set(recGuard)
    }
  }

  override def notifyBlockChanged(world: World, x: Int, y: Int, z: Int) {
    scanConnectedBlocks(BlockRef(world, x, y, z), true)._1 foreach (_.invalidateConnection())
  }

  override def recalculateConnectionInfo(te: IPressureInject, side: ForgeDirection) =
    ConnectionInfo(te, side, scanConnectedBlocks(BlockRef(te.getWorld, te.getXCoord, te.getYCoord, te.getZCoord), false)._2)

  def getPipeConnections(ref: BlockRef): List[ForgeDirection] =
    (for {
      (dir, target) <- ref.neighbours
      world <- target.world
      conn <- target.getBlock[IPressureConnectableBlock]
      if conn.canConnectFrom(world, target.x, target.y, target.z, dir.getOpposite)
    } yield dir).toList

  def getPipeConnections(w: IBlockAccess, x: Int, y: Int, z: Int): List[ForgeDirection] = {
    (ForgeDirection.VALID_DIRECTIONS filter { dir =>
      val blockId = w.getBlockId(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)
      val block = Block.blocksList(blockId)
      block != null && block.isInstanceOf[IPressureConnectableBlock] &&
        block.asInstanceOf[IPressureConnectableBlock].canConnectFrom(w, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite)
    }).toList
  }

}
