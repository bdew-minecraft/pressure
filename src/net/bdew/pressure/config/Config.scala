/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.config

import java.io.File

import net.bdew.lib.gui.GuiHandler
import net.minecraftforge.common.config.Configuration

object Config {
  val guiHandler = new GuiHandler

  def load(cfg: File): Configuration = {
    val c = new Configuration(cfg)
    c.load()
    try {
      Items.load()
      Blocks.load()
      Machines.load()
    } finally {
      c.save()
    }

    return c
  }
}