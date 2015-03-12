/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.render

import net.bdew.lib.block.BlockRef
import net.bdew.lib.render.{BaseBlockRenderHandler, RotatedBlockRenderer}
import net.bdew.lib.rotate.BaseRotatableBlock
import net.bdew.pressure.pressurenet.Helper
import net.bdew.pressure.render.RenderHelper._
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.world.IBlockAccess

object ValveRenderer extends BaseBlockRenderHandler {
  override def renderInventoryBlock(block: Block, metadata: Int, modelId: Int, renderer: RenderBlocks) =
    RotatedBlockRenderer.renderInventoryBlock(block, metadata, modelId, renderer)

  override def renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks) = {
    RotatedBlockRenderer.renderWorldBlock(world, x, y, z, block, modelId, renderer)
    val ref = BlockRef(x, y, z)
    val dir = block.asInstanceOf[BaseRotatableBlock].getFacing(world, x, y, z)
    val pos = P3d(x, y, z)
    if (Helper.canPipeConnectTo(world, ref.neighbour(dir), dir.getOpposite))
      PipeRenderer.renderPipeSection(world, pos, dir, renderer)
    if (Helper.canPipeConnectTo(world, ref.neighbour(dir.getOpposite), dir))
      PipeRenderer.renderPipeSection(world, pos, dir.getOpposite, renderer)
    true
  }
}
