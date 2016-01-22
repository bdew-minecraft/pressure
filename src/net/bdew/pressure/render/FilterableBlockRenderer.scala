/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.render

import net.bdew.lib.Misc
import net.bdew.lib.render.connected.ConnectedHelper.{EdgeDraw, RectF, Vec3F}
import net.bdew.lib.render.{BaseBlockRenderHandler, RenderUtils}
import net.bdew.pressure.blocks.BlockFilterable
import net.minecraft.block.Block
import net.minecraft.client.renderer.{RenderBlocks, Tessellator}
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection

object FilterableBlockRenderer extends BaseBlockRenderHandler {
  val filterIconDraw =
    (for (dir <- ForgeDirection.VALID_DIRECTIONS)
      yield (dir, new EdgeDraw(RectF(0.35F, 0.35F, 0.65F, 0.65F), dir))).toMap

  override def renderInventoryBlock(block: Block, metadata: Int, modelId: Int, renderer: RenderBlocks) =
    RenderUtils.renderSimpleBlockItem(block, metadata, renderer)

  def renderFilterLayer(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block): Unit = {
    Misc.asInstanceOpt(block, classOf[BlockFilterable[_]]) foreach { bf =>
      for {
        dir <- ForgeDirection.VALID_DIRECTIONS
        (icon, color) <- bf.getFilterIcon(world, x, y, z, dir)
      } {
        Tessellator.instance.setColorOpaque_I(color)
        filterIconDraw(dir).doDraw(Vec3F(x, y, z), icon)
        Tessellator.instance.setColorOpaque_F(1, 1, 1)
      }
    }
  }

  override def renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks): Boolean = {
    renderer.renderStandardBlock(block, x, y, z)
    renderFilterLayer(world, x, y, z, block)
    true
  }
}
