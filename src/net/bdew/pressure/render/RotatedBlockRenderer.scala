/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.render

import cpw.mods.fml.client.registry.{ISimpleBlockRenderingHandler, RenderingRegistry}
import net.bdew.lib.Misc
import net.bdew.lib.render.connected.ConnectedHelper.{EdgeDraw, RectF, Vec3F}
import net.bdew.pressure.blocks.BlockFilterable
import net.minecraft.block.Block
import net.minecraft.client.renderer.{RenderBlocks, Tessellator}
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.opengl.GL11

object RotatedBlockRenderer extends ISimpleBlockRenderingHandler {
  val id = RenderingRegistry.getNextAvailableRenderId
  RenderingRegistry.registerBlockHandler(this)

  override def getRenderId = id
  override def shouldRender3DInInventory(model: Int) = true

  val filterIconDraw =
    (for (dir <- ForgeDirection.VALID_DIRECTIONS)
    yield (dir, new EdgeDraw(RectF(0.35F, 0.35F, 0.65F, 0.65F), dir))).toMap

  def doRenderItemSide(d: ForgeDirection, r: RenderBlocks, block: Block, meta: Int) = {
    val icon = r.getBlockIconFromSideAndMetadata(block, d.ordinal(), meta)
    Tessellator.instance.setNormal(d.offsetX, d.offsetY, d.offsetZ)
    d match {
      case ForgeDirection.DOWN => r.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon)
      case ForgeDirection.UP => r.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon)
      case ForgeDirection.NORTH => r.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon)
      case ForgeDirection.SOUTH => r.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon)
      case ForgeDirection.WEST => r.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon)
      case ForgeDirection.EAST => r.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon)
      case _ => sys.error("Invalid side")
    }
  }

  override def renderInventoryBlock(block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks) {
    val tessellator = Tessellator.instance
    GL11.glTranslatef(-0.5F, -0.5F, -0.5F)

    for (side <- ForgeDirection.VALID_DIRECTIONS) {
      tessellator.startDrawingQuads()
      doRenderItemSide(side, renderer, block, metadata)
      tessellator.draw()
    }

    GL11.glTranslatef(0.5F, 0.5F, 0.5F)
  }

  override def renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks): Boolean = {
    val rotation = world.getBlockMetadata(x, y, z) & 7

    rotation match {
      case 0 =>
        renderer.uvRotateEast = 3
        renderer.uvRotateWest = 3
        renderer.uvRotateSouth = 3
        renderer.uvRotateNorth = 3
      case 2 =>
        renderer.uvRotateSouth = 1
        renderer.uvRotateNorth = 2
      case 3 =>
        renderer.uvRotateSouth = 2
        renderer.uvRotateNorth = 1
        renderer.uvRotateTop = 3
        renderer.uvRotateBottom = 3
      case 4 =>
        renderer.uvRotateEast = 1
        renderer.uvRotateWest = 2
        renderer.uvRotateTop = 2
        renderer.uvRotateBottom = 1
      case 5 =>
        renderer.uvRotateEast = 2
        renderer.uvRotateWest = 1
        renderer.uvRotateTop = 1
        renderer.uvRotateBottom = 2
      case _ =>
    }
    renderer.renderStandardBlock(block, x, y, z)
    renderer.uvRotateEast = 0
    renderer.uvRotateWest = 0
    renderer.uvRotateSouth = 0
    renderer.uvRotateNorth = 0
    renderer.uvRotateTop = 0
    renderer.uvRotateBottom = 0

    Misc.asInstanceOpt(block, classOf[BlockFilterable[_]]) map { bf =>
      for {
        dir <- ForgeDirection.VALID_DIRECTIONS
        (icon, color) <- bf.getFilterIcon(world, x, y, z, dir)
      } {
        Tessellator.instance.setColorOpaque_I(color)
        filterIconDraw(dir).doDraw(Vec3F(x, y, z), icon)
        Tessellator.instance.setColorOpaque_F(1, 1, 1)
      }
    }

    true
  }
}