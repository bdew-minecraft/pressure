/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.misc

import net.bdew.lib.Misc
import net.bdew.lib.block.BlockRef
import net.bdew.pressure.Pressure
import net.bdew.pressure.api._
import net.bdew.pressure.blocks.BlockPipe
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

import scala.util.DynamicVariable

object InternalPressureExtension extends IPressureExtension {
  override def canPipeConnectTo(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    Option(w.getBlock(x, y, z)) flatMap {
      Misc.asInstanceOpt(_, classOf[IPressureConnectableBlock])
    } exists {
      _.canConnectTo(w, x, y, z, side)
    }

  override def canPipeConnectFrom(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) = isConnectableBlock(w, x, y, z)

  override def isConnectableBlock(w: IBlockAccess, x: Int, y: Int, z: Int) =
    Option(w.getBlock(x, y, z)) exists (_.isInstanceOf[IPressureConnectableBlock])

  override def tryPlacePipe(w: World, x: Int, y: Int, z: Int, p: EntityPlayerMP) = {
    if (w.isAirBlock(x, y, z) || (Option(w.getBlock(x, y, z)) exists (_.isReplaceable(w, x, y, z)))) {
      w.setBlock(x, y, z, BlockPipe, 0, 3)
      true
    } else false
  }
}

object Helper extends IPressureHelper {
  var extensions = List.empty[IPressureExtension]

  registerExtension(InternalPressureExtension)

  val recursionGuard = new DynamicVariable(Set.empty[IPressureConnection])

  def scanConnectedBlocks(w: IBlockAccess, start: BlockRef, forceNeighbours: Boolean) = {
    val seen = collection.mutable.Set.empty[BlockRef]
    val queue = collection.mutable.Queue(start)

    if (forceNeighbours)
      queue ++= start.neighbours.values

    val inputs = collection.mutable.Set.empty[IPressureInject]
    val outputs = collection.mutable.Set.empty[IPressureEject]

    while (queue.nonEmpty) {
      val current = queue.dequeue()
      seen.add(current)
      if (isConnectableBlock(w, current))
        queue.enqueue(getPipeConnections(w, current) map current.neighbour filterNot seen.contains: _*)
      current.tile(w) collect {
        case t: IPressureInject =>
          inputs.add(t)
          queue.enqueue(ForgeDirection.VALID_DIRECTIONS
            filter (dir =>
            current.getBlock[IPressureConnectableBlock](w) exists (
              _.canConnectTo(w, current.x, current.y, current.z, dir)))
            map current.neighbour
            filterNot seen.contains: _*)
        case t: IPressureEject =>
          outputs.add(t)
          queue.enqueue(ForgeDirection.VALID_DIRECTIONS
            filter (dir =>
            current.getBlock[IPressureConnectableBlock](w) exists (
              _.canConnectTo(w, current.x, current.y, current.z, dir)))
            map current.neighbour
            filterNot seen.contains: _*)
      }
    }
    (inputs.toSet, outputs.toSet, seen)
  }

  override def notifyBlockChanged(world: World, x: Int, y: Int, z: Int) {
    if (!world.isRemote)
      scanConnectedBlocks(world, BlockRef(x, y, z), true)._1 foreach (_.invalidateConnection())
  }

  override def recalculateConnectionInfo(te: IPressureInject, side: ForgeDirection) =
    if (te.getWorld.isRemote) {
      Pressure.logWarn("Attempt to generate ConnectionInfo on client side from %s. This is a bug.", te)
      null
    } else PressureConnection(te, side, scanConnectedBlocks(te.getWorld, BlockRef(te.getXCoord, te.getYCoord, te.getZCoord), false)._2)

  def getPipeConnections(w: IBlockAccess, ref: BlockRef): List[ForgeDirection] =
    (for {
      (dir, target) <- ref.neighbours
      if canPipeConnectFrom(w, ref, dir) && canPipeConnectTo(w, target, dir.getOpposite)
    } yield dir).toList

  def getPipeConnections(w: IBlockAccess, x: Int, y: Int, z: Int): List[ForgeDirection] = {
    ForgeDirection.VALID_DIRECTIONS.toList filter { dir =>
      canPipeConnectFrom(w, x, y, z, dir) && canPipeConnectTo(w, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite)
    }
  }

  def canPipeConnectTo(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    extensions.exists(_.canPipeConnectTo(w, x, y, z, side))

  def canPipeConnectFrom(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    extensions.exists(_.canPipeConnectFrom(w, x, y, z, side))

  def isConnectableBlock(w: IBlockAccess, x: Int, y: Int, z: Int) =
    extensions.exists(_.isConnectableBlock(w, x, y, z))

  def canPipeConnectTo(w: IBlockAccess, ref: BlockRef, side: ForgeDirection) =
    extensions.exists(_.canPipeConnectTo(w, ref.x, ref.y, ref.z, side))

  def canPipeConnectFrom(w: IBlockAccess, ref: BlockRef, side: ForgeDirection) =
    extensions.exists(_.canPipeConnectFrom(w, ref.x, ref.y, ref.z, side))

  def isConnectableBlock(w: IBlockAccess, ref: BlockRef) =
    extensions.exists(_.isConnectableBlock(w, ref.x, ref.y, ref.z))

  override def tryPlacePipe(w: World, x: Int, y: Int, z: Int, p: EntityPlayerMP) =
    extensions.exists(_.tryPlacePipe(w, x, y, z, p))

  override def registerExtension(ext: IPressureExtension) = extensions :+= ext
}
