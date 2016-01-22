/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router.gui

import net.bdew.lib.Misc
import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Color, Point, Rect, Texture}
import net.bdew.pressure.blocks.router.TileRouter
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.FluidStack

import scala.collection.mutable

class RouterFilterIcon(p: Point, te: TileRouter, side: ForgeDirection) extends Widget {
  override val rect = new Rect(p, 16, 16)
  override def draw(mouse: Point) {
    if (te.sideFilters.isSet(side)) {
      val fluid = te.sideFilters.get(side)
      parent.drawTexture(rect, Texture(Texture.BLOCKS, Misc.getFluidIcon(fluid)), Color.fromInt(Misc.getFluidColor(fluid)))
    }
  }
  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) = {
    if (te.sideFilters.isSet(side)) {
      val fluid = te.sideFilters.get(side)
      tip += Misc.toLocalF("pressure.gui.tank.filter", fluid.getLocalizedName(new FluidStack(fluid, 1)))
    } else {
      tip += Misc.toLocal("pressure.gui.tank.nofilter")
    }
    tip += EnumChatFormatting.GRAY + Misc.toLocal("pressure.gui.tank.filter.tip1") + EnumChatFormatting.RESET
    tip += EnumChatFormatting.GRAY + Misc.toLocal("pressure.gui.tank.filter.tip2") + EnumChatFormatting.RESET
  }
}
