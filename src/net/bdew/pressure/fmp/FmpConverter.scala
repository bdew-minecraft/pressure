/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp

import java.util

import codechicken.lib.vec.BlockCoord
import codechicken.multipart.MultiPartRegistry.IPartConverter
import net.bdew.lib.block.BlockRef
import net.bdew.pressure.blocks.BlockPipe
import net.bdew.pressure.blocks.valves.check.BlockCheckValve
import net.bdew.pressure.blocks.valves.sensor.BlockPipeSensor
import net.bdew.pressure.fmp.parts.{CheckValvePart, PipePart, PipeSensorPart}
import net.minecraft.world.World

object FmpConverter extends IPartConverter {
  override def blockTypes = util.Arrays.asList(BlockPipe, BlockCheckValve, BlockPipeSensor)
  override def convert(world: World, pos: BlockCoord) = {
    BlockRef(pos.x, pos.y, pos.z).block(world) match {
      case Some(BlockPipe) => new PipePart
      case Some(BlockCheckValve) => new CheckValvePart(world.getBlockMetadata(pos.x, pos.y, pos.z))
      case Some(BlockPipeSensor) => new PipeSensorPart(world.getBlockMetadata(pos.x, pos.y, pos.z))
      case _ => null
    }
  }
}
