/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.gui

import net.bdew.lib.Misc
import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Color, Point, Rect}
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fluids.FluidStack

import scala.collection.mutable

class WidgetFilterIcon(p: Point, te: TileTankController) extends Widget {
  override val rect = new Rect(p, 16, 16)
  override def draw(mouse: Point, partial: Float) {
    te.getFluidFilter.foreach { fluid =>
      parent.drawTexture(rect, Misc.getFluidIcon(fluid), Color.fromInt(Misc.getFluidColor(fluid)))
    }
  }
  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) = {
    tip += te.getFluidFilter map { fluid =>
      Misc.toLocalF("pressure.gui.tank.filter", fluid.getLocalizedName(new FluidStack(fluid, 1)))
    } getOrElse Misc.toLocal("pressure.gui.tank.nofilter")
    tip += TextFormatting.GRAY + Misc.toLocal("pressure.gui.tank.filter.tip1") + TextFormatting.RESET
    tip += TextFormatting.GRAY + Misc.toLocal("pressure.gui.tank.filter.tip2") + TextFormatting.RESET
  }
}
