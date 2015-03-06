/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router.gui

import net.bdew.lib.Misc
import net.bdew.lib.gui._
import net.bdew.lib.gui.widgets.WidgetLabel
import net.bdew.pressure.Pressure
import net.bdew.pressure.blocks.router.TileRouter
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.util.ForgeDirection

class GuiRouter(val te: TileRouter, player: EntityPlayer) extends BaseScreen(new ContainerRouter(te, player), 176, 166) {
  val background = Texture(Pressure.modId, "textures/gui/router.png", rect)
  override def initGui() {
    super.initGui()
    widgets.add(new WidgetLabel(Misc.toLocal("pressure.gui.router.title"), 8, 6, Color.darkGray))
    for (dir <- ForgeDirection.VALID_DIRECTIONS)
      widgets.add(new RouterSideWidget(te, Point(26 + dir.ordinal() * 21, 18), dir))
  }
} 