/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router.gui

import java.util.Locale

import net.bdew.lib.Misc
import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect}
import net.bdew.pressure.PressureResourceProvider
import net.bdew.pressure.blocks.router.{RouterIcons, TileRouter}
import net.minecraft.util.EnumFacing

import scala.collection.mutable

class RouterSideIcon(p: Point, te: TileRouter, side: EnumFacing) extends Widget {
  val rect = new Rect(p, 16, 16)

  override def draw(mouse: Point, partial: Float) {
    parent.drawTexture(rect, RouterIcons.modeIcons(te.sideModes.get(side)), PressureResourceProvider.outputColors(side.ordinal()))
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    tip += "%s (%s)".format(Misc.toLocal("pressure.router.side." + side.ordinal()), Misc.toLocal("bdlib.multiblock.face." + side.toString.toLowerCase(Locale.US)))
    tip += Misc.toLocal("pressure.router.mode." + te.sideModes.get(side).toString.toLowerCase(Locale.US))
  }
}
