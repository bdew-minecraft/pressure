/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp

import codechicken.lib.vec.BlockCoord
import codechicken.multipart.TileMultipart
import net.bdew.lib.rotate.RotatedHelper
import net.bdew.pressure.api.IPressureExtension
import net.bdew.pressure.blocks.BlockPipe
import net.bdew.pressure.blocks.valves.check.BlockCheckValve
import net.bdew.pressure.blocks.valves.sensor.BlockPipeSensor
import net.bdew.pressure.fmp.parts.{CheckValvePart, PipePart, PipeSensorPart}
import net.bdew.pressure.fmp.traits.TConnectablePart
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

object FmpPressureExtension extends IPressureExtension {
  override def isConnectableBlock(world: IBlockAccess, x: Int, y: Int, z: Int) =
    FmpUtils.findTypedParts(world, x, y, z, classOf[TConnectablePart]).nonEmpty

  override def isTraversableBlock(world: IBlockAccess, x: Int, y: Int, z: Int) =
    FmpUtils.findTypedParts(world, x, y, z, classOf[TConnectablePart]).exists(_.isTraversable)

  override def canPipeConnectFrom(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    FmpUtils.findTypedParts(world, x, y, z, classOf[TConnectablePart]).exists(_.canConnectTo(side))

  override def canPipeConnectTo(world: IBlockAccess, x: Int, y: Int, z: Int, side: ForgeDirection) =
    FmpUtils.findTypedParts(world, x, y, z, classOf[TConnectablePart]).exists(_.canConnectTo(side))

  override def tryPlaceBlock(w: World, x: Int, y: Int, z: Int, block: Block, p: EntityPlayerMP) = {
    val pos = new BlockCoord(x, y, z)
    (block match {
      case BlockPipe => Some(new PipePart)
      case BlockCheckValve =>
        Some(new CheckValvePart(RotatedHelper.getFacingFromEntity(
          p, BlockCheckValve.getValidFacings, BlockCheckValve.getDefaultFacing)))
      case BlockPipeSensor =>
        Some(new PipeSensorPart(RotatedHelper.getFacingFromEntity(
          p, BlockPipeSensor.getValidFacings, BlockPipeSensor.getDefaultFacing)))
      case _ => None
    }) exists { part =>
      if (TileMultipart.getTile(w, pos) != null && TileMultipart.canPlacePart(w, pos, part)) {
        TileMultipart.addPart(w, pos, part)
        true
      } else false
    }
  }
}


