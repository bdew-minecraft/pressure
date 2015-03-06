/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router.gui

import net.bdew.lib.gui._
import net.bdew.lib.gui.widgets.WidgetSubContainer
import net.bdew.pressure.blocks.router.TileRouter
import net.minecraftforge.common.util.ForgeDirection

class RouterSideWidget(val te: TileRouter, p: Point, side: ForgeDirection) extends WidgetSubContainer(new Rect(p, 18, 58)) {
  add(new RouterSideIcon(Point(1, 1), te, side))
  add(new RouterFilterIcon(Point(1, 21), te, side))
  add(new RouterRSModeButton(Point(1, 41), te, side))
}
