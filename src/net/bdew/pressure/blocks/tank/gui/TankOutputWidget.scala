/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.gui

import net.bdew.lib.gui._
import net.bdew.lib.gui.widgets.{WidgetMultiPane, WidgetSubContainer}
import net.bdew.lib.multiblock.data.OutputConfigFluid
import net.bdew.lib.multiblock.gui.{WidgetOutputIcon, WidgetRSConfig}
import net.bdew.pressure.blocks.tank.controller.TileTankController

class TankOutputWidget(val te: TileTankController, p: Point, outSlot: Int) extends WidgetMultiPane(new Rect(p, 18, 38)) {
  add(new WidgetOutputIcon(Point(0, 0), te, outSlot))

  val emptyPane = addPane(new WidgetSubContainer(rect))
  val fluidPane = addPane(new WidgetRSConfig(te, outSlot, Point(0, 20)))

  def getActivePane =
    te.outputConfig.get(outSlot) match {
      case Some(x: OutputConfigFluid) => fluidPane
      case _ => emptyPane
    }
}
