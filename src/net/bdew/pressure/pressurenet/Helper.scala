/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.pressurenet

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.block.BlockFace
import net.bdew.lib.capabilities.{NoFactory, NoStorage}
import net.bdew.pressure.Pressure
import net.bdew.pressure.api._
import net.bdew.pressure.api.properties.IFilterable
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.capabilities.CapabilityManager

import scala.util.DynamicVariable

object Helper extends IPressureHelper {
  var extensions = List.empty[IPressureExtension]
  var filterableProviders = List.empty[IFilterableProvider]

  CapabilityManager.INSTANCE.register(classOf[IFilterable], new NoStorage[IFilterable], new NoFactory[IFilterable])

  registerExtension(InternalPressureExtension)
  registerIFilterableProvider(InternalPressureExtension)

  val recursionGuard = new DynamicVariable(Set.empty[IPressureConnection])

  def scanConnectedBlocks(w: IBlockAccess, start: BlockPos, face: EnumFacing, forceNeighbours: Boolean) = {
    val seen = collection.mutable.Set.empty[BlockPos]
    val queue = collection.mutable.Queue(BlockFace(start, face))

    if (forceNeighbours)
      for ((face, block) <- start.neighbours) queue.enqueue(BlockFace(block, face.getOpposite))
    else if (face != null && isConnectableBlock(w, start.offset(face)))
      queue.enqueue(BlockFace(start.offset(face), face.getOpposite))

    val inputs = collection.mutable.Set.empty[PressureInputFace]
    val outputs = collection.mutable.Set.empty[PressureOutputFace]

    while (queue.nonEmpty) {
      val current = queue.dequeue()

      if (isTraversableBlock(w, current.pos)) {
        seen.add(current.pos)
        queue ++= (
          getPipeConnections(w, current.pos)
            map (x => BlockFace(current.pos.offset(x), x.getOpposite))
            filterNot (x => seen.contains(x.pos))
          )
      }

      if (current.face != null && canPipeConnectTo(w, current.pos, current.face)) {
        val tile = w.getTileEntity(current.pos)
        if (tile.isInstanceOf[IPressureInject])
          inputs += PressureInputFace(tile.asInstanceOf[IPressureInject], current.face)
        if (tile.isInstanceOf[IPressureEject])
          outputs += PressureOutputFace(tile.asInstanceOf[IPressureEject], current.face)
      }
    }
    ScanResult(inputs.toSet, outputs.toSet, seen.toSet)
  }

  override def notifyBlockChanged(world: World, pos: BlockPos) {
    if (!world.isRemote)
      scanConnectedBlocks(world, pos, null, true).inputs foreach (_.invalidateConnection())
  }

  override def recalculateConnectionInfo(te: IPressureInject, side: EnumFacing) =
    if (te.pressureNodeWorld.isRemote) {
      Pressure.logWarn("Attempt to generate ConnectionInfo on client side from %s. This is a bug.", te)
      null
    } else {
      PressureConnection(te, side, scanConnectedBlocks(te.pressureNodeWorld, te.pressureNodePos, side, false).outputs)
    }

  def getPipeConnections(w: IBlockAccess, pos: BlockPos): List[EnumFacing] =
    (for {
      (dir, target) <- pos.neighbours
      if canPipeConnectFrom(w, pos, dir) && canPipeConnectTo(w, target, dir.getOpposite)
    } yield dir).toList

  def canPipeConnectFrom(w: IBlockAccess, pos: BlockPos, side: EnumFacing) =
    extensions.exists(_.canPipeConnectFrom(w, pos, side))

  def isConnectableBlock(w: IBlockAccess, pos: BlockPos) =
    extensions.exists(_.isConnectableBlock(w, pos))

  def isTraversableBlock(w: IBlockAccess, pos: BlockPos) =
    extensions.exists(_.isTraversableBlock(w, pos))

  def canPipeConnectTo(w: IBlockAccess, pos: BlockPos, side: EnumFacing) =
    extensions.exists(_.canPipeConnectTo(w, pos, side))

  override def tryPlaceBlock(w: World, pos: BlockPos, b: Block, p: EntityPlayerMP) =
    extensions.exists(_.tryPlaceBlock(w, pos, b, p))

  def getFilterableForWorldCoordinates(world: World, pos: BlockPos, side: EnumFacing): IFilterable = {
    for (fp <- filterableProviders)
      Option(fp.getFilterableForWorldCoordinates(world, pos, side)) map {
        return _
      }
    null
  }

  override def registerExtension(ext: IPressureExtension) = extensions :+= ext
  override def registerIFilterableProvider(provider: IFilterableProvider) = filterableProviders :+= provider
}
