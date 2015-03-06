/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director.gui

import net.bdew.lib.gui._
import net.bdew.lib.gui.widgets.WidgetSubContainer
import net.bdew.pressure.blocks.director.TileDirector
import net.minecraftforge.common.util.ForgeDirection

class DirectorSideWidget(val te: TileDirector, p: Point, side: ForgeDirection) extends WidgetSubContainer(new Rect(p, 18, 58)) {
  add(new DirectorSideIcon(Point(1, 1), te, side))
  add(new DirectorFilterIcon(Point(1, 21), te, side))
  add(new DirectorRSModeButton(Point(1, 41), te, side))
}
