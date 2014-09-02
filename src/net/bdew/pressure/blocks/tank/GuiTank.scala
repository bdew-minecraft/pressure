/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import net.bdew.lib.Misc
import net.bdew.lib.gui._
import net.bdew.lib.gui.widgets.{WidgetFluidGauge, WidgetLabel}
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.bdew.pressure.{Pressure, Textures}
import net.minecraft.entity.player.EntityPlayer

class GuiTank(val te: TileTankController, player: EntityPlayer) extends BaseScreen(new ContainerTank(te, player), 176, 166) {
  val background = Texture(Pressure.modId, "textures/gui/tank.png", rect)
  override def initGui() {
    super.initGui()
    widgets.add(new WidgetFluidGauge(new Rect(8, 19, 16, 58), Textures.tankOverlay, te.tank))
    widgets.add(new WidgetLabel(Misc.toLocal("pressure.gui.tank.title"), 8, 6, Color.darkgray))
    widgets.add(new WidgetLabel(Misc.toLocal("pressure.gui.tank.manual"), 103, 22, Color.darkgray))
  }
}
