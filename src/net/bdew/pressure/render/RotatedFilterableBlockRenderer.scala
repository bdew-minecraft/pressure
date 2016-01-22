/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.render

import net.bdew.lib.render.{BaseBlockRenderHandler, RenderUtils, RotatedBlockRenderer}
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.world.IBlockAccess

object RotatedFilterableBlockRenderer extends BaseBlockRenderHandler {
  override def renderInventoryBlock(block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks) =
    RenderUtils.renderSimpleBlockItem(block, metadata, renderer)

  override def renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks): Boolean = {
    RotatedBlockRenderer.renderWorldBlock(world, x, y, z, block, modelId, renderer)
    FilterableBlockRenderer.renderFilterLayer(world, x, y, z, block)
    true
  }
}

