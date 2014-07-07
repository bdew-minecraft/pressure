/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.fmp

import java.util

import codechicken.lib.vec.{Cuboid6, Vector3}
import codechicken.multipart.{TCuboidPart, TMultiPart, TNormalOcclusion}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.pressure.blocks.BlockPipe
import net.bdew.pressure.misc.Helper
import net.bdew.pressure.render.PipeRenderer
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.item.ItemStack

class PipePart extends TCuboidPart with TNormalOcclusion {
  override def getType = "bdew.pressure.pipe"

  override def getBounds = new Cuboid6(0.2, 0.2, 0.2, 0.8, 0.8, 0.8)
  override def getOcclusionBoxes = util.Arrays.asList(getBounds)

  override def onWorldJoin() = if (!world.isRemote) Helper.notifyBlockChanged(world, x, y, z)
  override def onWorldSeparate() = if (!world.isRemote) Helper.notifyBlockChanged(world, x, y, z)
  override def onPartChanged(part: TMultiPart) = Helper.notifyBlockChanged(world, x, y, z)

  override def getDrops = util.Arrays.asList(new ItemStack(BlockPipe))

  @SideOnly(Side.CLIENT)
  override def renderStatic(pos: Vector3, pass: Int) = {
    if (pass == 0) {
      PipeRenderer.instance.renderWorldBlock(world, x, y, z, BlockPipe, PipeRenderer.id, new RenderBlocks(world))
      true
    } else false
  }

  //  override def drawBreaking(renderBlocks: RenderBlocks) {
  //    PipeRenderer.instance.renderWorldBlock(world, x, y, z, BlockPipe, PipeRenderer.id, renderBlocks)
  //  }
}
