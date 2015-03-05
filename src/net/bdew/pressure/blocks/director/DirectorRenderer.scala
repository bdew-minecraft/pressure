/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director

import net.bdew.lib.Misc
import net.bdew.lib.render.connected.ConnectedHelper
import net.bdew.lib.render.connected.ConnectedHelper.Vec3F
import net.bdew.lib.render.{BaseBlockRenderHandler, RenderUtils}
import net.bdew.pressure.PressureResourceProvider
import net.minecraft.block.Block
import net.minecraft.client.renderer.{RenderBlocks, Tessellator}
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection

object DirectorRenderer extends BaseBlockRenderHandler {
  override def renderInventoryBlock(block: Block, metadata: Int, modelId: Int, renderer: RenderBlocks): Unit =
    RenderUtils.renderSimpleBlockItem(block, metadata, renderer)

  override def renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks): Boolean = {
    renderer.renderStandardBlock(block, x, y, z)
    for {
      tile <- Misc.asInstanceOpt(world.getTileEntity(x, y, z), classOf[TileDirector])
      face <- ForgeDirection.VALID_DIRECTIONS
      if block.shouldSideBeRendered(world, x + face.offsetX, y + face.offsetY, z + face.offsetZ, face.ordinal())
    } {
      val color = PressureResourceProvider.outputColors(face.ordinal())
      Tessellator.instance.setColorOpaque_F(color.r, color.g, color.b)
      ConnectedHelper.draw(face, 8).doDraw(Vec3F(x, y, z), DirectorIcons.overlays(tile.sideModes.get(face)))
    }
    true
  }
}
