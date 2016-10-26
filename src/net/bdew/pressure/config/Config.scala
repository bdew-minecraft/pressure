/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.config

import java.io.File

import net.bdew.lib.gui.GuiHandler
import net.minecraftforge.common.config.Configuration

object Config {
  val guiHandler = new GuiHandler

  var showCanisters = true
  var showFluidName = false

  def load(cfg: File) {
    val c = new Configuration(cfg)
    c.load()
    try {
      showCanisters = c.get(Configuration.CATEGORY_GENERAL, "Add filled canisters to JEI", true).getBoolean(false)
      showFluidName = c.get(Configuration.CATEGORY_GENERAL, "Show fluid identifier on canisters", false).getBoolean(false)
    } finally {
      c.save()
    }
  }
}