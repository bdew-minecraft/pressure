/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router.gui

import java.util.Locale

import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect}
import net.bdew.lib.{Client, Misc}
import net.bdew.pressure.Textures
import net.bdew.pressure.blocks.router.{MachineRouter, TileRouter}
import net.bdew.pressure.network.NetworkHandler
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.{EnumFacing, ResourceLocation}

import scala.collection.mutable

class RouterRSModeButton(p: Point, te: TileRouter, side: EnumFacing) extends Widget {
  val rect = new Rect(p, 16, 16)
  val iconRect = new Rect(p +(1, 1), 14, 14)

  override def draw(mouse: Point) {
    if (rect.contains(mouse))
      parent.drawTexture(rect, Textures.Button16.hover)
    else
      parent.drawTexture(rect, Textures.Button16.base)

    parent.drawTexture(iconRect, Textures.iconRSMode(te.sideControl.get(side)))
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    tip += Misc.toLocal("bdlib.rsmode." + te.sideControl.get(side).toString.toLowerCase(Locale.US))
  }

  override def mouseClicked(p: Point, button: Int) {
    Client.minecraft.getSoundHandler.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F))
    NetworkHandler.sendToServer(MsgSetRouterSideControl(side, MachineRouter.rsModeOrder(te.sideControl.get(side))))
  }
}
