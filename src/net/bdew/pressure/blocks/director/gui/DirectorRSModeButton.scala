/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director.gui

import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect, Texture}
import net.bdew.lib.{Client, Misc}
import net.bdew.pressure.Textures
import net.bdew.pressure.blocks.director.{MachineDirector, TileDirector}
import net.bdew.pressure.network.NetworkHandler
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.ForgeDirection

import scala.collection.mutable

class DirectorRSModeButton(p: Point, te: TileDirector, side: ForgeDirection) extends Widget {
  val rect = new Rect(p, 16, 16)
  val iconRect = new Rect(p +(1, 1), 14, 14)

  var icon: Texture = null

  override def draw(mouse: Point) {
    if (rect.contains(mouse))
      parent.drawTexture(rect, Textures.Button16.hover)
    else
      parent.drawTexture(rect, Textures.Button16.base)

    parent.drawTexture(iconRect, Textures.iconRSMode(te.sideControl.get(side)))
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    tip += Misc.toLocal("bdlib.rsmode." + te.sideControl.get(side).toString.toLowerCase)
  }

  override def mouseClicked(p: Point, button: Int) {
    Client.minecraft.getSoundHandler.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F))
    NetworkHandler.sendToServer(MsgSetDirectorSideControl(side, MachineDirector.rsModeOrder(te.sideControl.get(side))))
  }
}
