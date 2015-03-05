/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.pressurenet

import net.bdew.lib.block.BlockRef
import net.bdew.pressure.Pressure
import net.bdew.pressure.api._
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

import scala.util.DynamicVariable

object Helper extends IPressureHelper {
  var extensions = List.empty[IPressureExtension]
  var filterable = List.empty[IFilterableProvider]

  registerExtension(InternalPressureExtension)
  registerIFilterableProvider(InternalPressureExtension)

  val recursionGuard = new DynamicVariable(Set.empty[IPressureConnection])

  def scanConnectedBlocks(w: IBlockAccess, start: BlockRef, face: ForgeDirection, forceNeighbours: Boolean) = {
    val seen = collection.mutable.Set.empty[BlockRefFace]
    val queue = collection.mutable.Queue(BlockRefFace(start, face))

    if (forceNeighbours)
      for ((face, block) <- start.neighbours) queue.enqueue(BlockRefFace(block, face.getOpposite))
    else if (face != ForgeDirection.UNKNOWN && isConnectableBlock(w, start.neighbour(face)))
      queue.enqueue(BlockRefFace(start.neighbour(face), face.getOpposite))

    val inputs = collection.mutable.Set.empty[PressureInputFace]
    val outputs = collection.mutable.Set.empty[PressureOutputFace]

    while (queue.nonEmpty) {
      val current = queue.dequeue()
      seen.add(current)
      if (isTraversableBlock(w, current))
        queue ++= (getPipeConnections(w, current) map (x => BlockRefFace(current.neighbour(x), x.getOpposite)) filterNot seen.contains)
      if (current.face != ForgeDirection.UNKNOWN && canPipeConnectTo(w, current, current.face)) {
        current.tile(w) collect {
          case t: IPressureInject =>
            inputs.add(PressureInputFace(t, current.face))
          case t: IPressureEject =>
            outputs.add(PressureOutputFace(t, current.face))
        }
      }
    }
    ScanResult(inputs.toSet, outputs.toSet, seen.toSet)
  }

  override def notifyBlockChanged(world: World, x: Int, y: Int, z: Int) {
    if (!world.isRemote)
      scanConnectedBlocks(world, BlockRef(x, y, z), ForgeDirection.UNKNOWN, true).inputs foreach (_.invalidateConnection())
  }

  override def recalculateConnectionInfo(te: IPressureInject, side: ForgeDirection) =
    if (te.getWorld.isRemote) {
      Pressure.logWarn("Attempt to generate ConnectionInfo on client side from %s. This is a bug.", te)
      null
    } else {
      PressureConnection(te, side, scanConnectedBlocks(te.getWorld, BlockRef(te.getXCoord, te.getYCoord, te.getZCoord), side, false).outputs)
    }

  def getPipeConnections(w: IBlockAccess, ref: BlockRef): List[ForgeDirection] =
    (for {
      (dir, target) <- ref.neighbours
      if canPipeConnectFrom(w, ref, dir) && canPipeConnectTo(w, target, dir.getOpposite)
    } yield dir).toList

  def getPipeConnections(w: IBlockAccess, x: Int, y: Int, z: Int): List[ForgeDirection] = {
    ForgeDirection.VALID_DIRECTIONS.toList filter { dir =>
      y + dir.offsetY >= 0 && y + dir.offsetY < 256 && canPipeConnectFrom(w, x, y, z, dir) &&
        canPipeConnectTo(w, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite)
    }
  }

  def canPipeConnectTo(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    extensions.exists(_.canPipeConnectTo(w, x, y, z, side))

  def canPipeConnectFrom(w: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    extensions.exists(_.canPipeConnectFrom(w, x, y, z, side))

  def isConnectableBlock(w: IBlockAccess, x: Int, y: Int, z: Int) =
    extensions.exists(_.isConnectableBlock(w, x, y, z))

  def isTraversableBlock(w: IBlockAccess, x: Int, y: Int, z: Int) =
    extensions.exists(_.isTraversableBlock(w, x, y, z))

  def canPipeConnectTo(w: IBlockAccess, ref: BlockRef, side: ForgeDirection) =
    extensions.exists(_.canPipeConnectTo(w, ref.x, ref.y, ref.z, side))

  def canPipeConnectFrom(w: IBlockAccess, ref: BlockRef, side: ForgeDirection) =
    extensions.exists(_.canPipeConnectFrom(w, ref.x, ref.y, ref.z, side))

  def isConnectableBlock(w: IBlockAccess, ref: BlockRef) =
    extensions.exists(_.isConnectableBlock(w, ref.x, ref.y, ref.z))

  def isTraversableBlock(w: IBlockAccess, ref: BlockRef) =
    extensions.exists(_.isTraversableBlock(w, ref.x, ref.y, ref.z))

  override def tryPlacePipe(w: World, x: Int, y: Int, z: Int, p: EntityPlayerMP) =
    extensions.exists(_.tryPlacePipe(w, x, y, z, p))

  def getFilterableForWorldCoordinates(world: World, x: Int, y: Int, z: Int, side: Int): IFilterable = {
    for (fp <- filterable)
      Option(fp.getFilterableForWorldCoordinates(world, x, y, z, side)) map {
        return _
      }
    null
  }

  override def registerExtension(ext: IPressureExtension) = extensions :+= ext
  override def registerIFilterableProvider(provider: IFilterableProvider) = filterable :+= provider
}
