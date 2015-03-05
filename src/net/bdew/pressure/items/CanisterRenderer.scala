/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items

import net.bdew.lib.gui.Color
import net.bdew.lib.{Client, Misc}
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.{ItemRenderType, ItemRendererHelper}
import org.lwjgl.opengl.GL11

object CanisterRenderer extends IItemRenderer {
  def handleRenderType(item: ItemStack, kind: ItemRenderType): Boolean = kind == ItemRenderType.INVENTORY
  def shouldUseRenderHelper(kind: ItemRenderType, item: ItemStack, helper: ItemRendererHelper): Boolean = false
  def renderItem(kind: ItemRenderType, item: ItemStack, data: AnyRef*): Unit = {
    val tessellator: Tessellator = Tessellator.instance

    val icon = Canister.getIcon(item, 0)

    GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT)

    GL11.glDisable(GL11.GL_LIGHTING)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    GL11.glEnable(GL11.GL_BLEND)

    tessellator.startDrawingQuads()
    tessellator.addVertexWithUV(0, 16, 0, icon.getMinU, icon.getMaxV)
    tessellator.addVertexWithUV(16, 16, 0, icon.getMaxU, icon.getMaxV)
    tessellator.addVertexWithUV(16, 0, 0, icon.getMaxU, icon.getMinV)
    tessellator.addVertexWithUV(0, 0, 0, icon.getMinU, icon.getMinV)
    tessellator.draw()

    GL11.glDisable(GL11.GL_BLEND)

    val fl = Canister.getFluid(item)
    if (fl != null) {

      GL11.glDisable(GL11.GL_TEXTURE_2D)
      GL11.glColor3d(0.7, 0.7, 0.7)
      tessellator.startDrawingQuads()
      tessellator.addVertex(0, 16, 1)
      tessellator.addVertex(5, 16, 1)
      tessellator.addVertex(5, 0, 1)
      tessellator.addVertex(0, 0, 1)
      tessellator.draw()
      GL11.glEnable(GL11.GL_TEXTURE_2D)

      val icon = Misc.getFluidIcon(fl)
      Color.fromInt(Misc.getFluidColor(fl)).activate()
      Client.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
      val fill = 15F * fl.amount / Canister.capacity
      val u = icon.getInterpolatedU(0)
      val U = icon.getInterpolatedU(4)
      val v = icon.getInterpolatedV(1)
      val V = icon.getInterpolatedV(1 + fill)
      tessellator.startDrawingQuads()
      tessellator.addVertexWithUV(0.5, 15.5, 1, u, V)
      tessellator.addVertexWithUV(4.5, 15.5, 1, U, V)
      tessellator.addVertexWithUV(4.5, 15.5 - fill, 1, U, v)
      tessellator.addVertexWithUV(0.5, 15.5 - fill, 1, u, v)
      tessellator.draw()

      GL11.glColor3d(1, 1, 1)
    }

    GL11.glPopAttrib()
  }
}
