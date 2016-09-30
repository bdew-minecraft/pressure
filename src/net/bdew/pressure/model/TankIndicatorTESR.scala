/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.model

import net.bdew.lib.gui.Color
import net.bdew.lib.render.primitive.{Texture, UV, Vertex}
import net.bdew.lib.render.{Cuboid, WorldQuadRender}
import net.bdew.lib.{Client, Misc}
import net.bdew.pressure.blocks.tank.blocks.{BlockTankIndicator, TileTankIndicator}
import net.minecraft.client.renderer._
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.EnumFacing
import org.lwjgl.opengl.GL11

object TankIndicatorTESR extends TileEntitySpecialRenderer[TileTankIndicator] {
  def quad(face: EnumFacing, low: Float, high: Float) = face match {
    case EnumFacing.SOUTH => Cuboid.face(Vertex(-1 / 16F, low - 0.5f, 0.5001F), Vertex(1 / 16F, high - 0.5f, 0.5001F), face)
    case EnumFacing.NORTH => Cuboid.face(Vertex(-1 / 16F, low - 0.5f, -0.5001F), Vertex(1 / 16F, high - 0.5f, -0.5001F), face)
    case EnumFacing.EAST => Cuboid.face(Vertex(0.5001F, low - 0.5f, -1 / 16F), Vertex(0.5001F, high - 0.5f, 1 / 16F), face)
    case EnumFacing.WEST => Cuboid.face(Vertex(-0.5001F, low - 0.5f, -1 / 16F), Vertex(-0.5001F, high - 0.5f, 1 / 16F), face)
    case _ => null // shutup compiler
  }

  override def renderTileEntityAt(te: TileTankIndicator, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int): Unit = {
    // No world or no relevant faces are visible - nothing to do here
    if (!te.hasWorldObj || !BlockTankIndicator.Position.faces.exists(f => BlockTankIndicator.shouldSideBeRendered(te.getWorld.getBlockState(te.getPos), te.getWorld, te.getPos, f))) return
    for {
      core <- te.getCore
      fluidStack <- Option(core.tank.getFluid)
      fluid <- Option(fluidStack.getFluid) if fluidStack.amount > 0
    } {
      val sprite = Client.textureMapBlocks.getAtlasSprite(fluid.getStill(fluidStack).toString)

      GlStateManager.pushMatrix()
      GlStateManager.translate(x + 0.5f, y + 0.5f, z + 0.5f)
      GlStateManager.disableLighting()
      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240)
      Color.fromInt(fluid.getColor(fluidStack)).activate()
      bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

      val quads = for {
        face <- BlockTankIndicator.Position.faces if BlockTankIndicator.shouldSideBeRendered(te.getWorld.getBlockState(te.getPos), te.getWorld, te.getPos, face)
        (below, above) <- te.cachedPosition.get(face)
      } yield {
        val low = if (below > 0) 0F else 0.125F
        val high = if (above > 0) 1F else 0.875F
        val span = high - low
        val blockVal = core.tank.getCapacity.toFloat / (above + below + 1)
        val myFluid = Misc.clamp(fluidStack.amount.toFloat - below * blockVal, 0F, blockVal) / blockVal
        quad(face, low, low + span * myFluid).withTexture(Texture(sprite, UV(7, low * 16f), UV(9, high * 16f)))
      }

      WorldQuadRender.renderQuads(quads)

      GlStateManager.enableLighting()
      GlStateManager.disableBlend()
      GlStateManager.popMatrix()
    }
  }
}
