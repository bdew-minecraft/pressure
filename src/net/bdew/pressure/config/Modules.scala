/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.config

object Modules {
  lazy val cfg = Tuning.getSection("Modules")

  object TankBlock {
    lazy val cfg = Modules.cfg.getSection("TankBlock")
    lazy val capacity = cfg.getInt("Capacity")
  }

  object Drain {
    lazy val cfg = Modules.cfg.getSection("Drain")
    lazy val makeXPOrbs = cfg.getBoolean("MakeXPOrbs")
    lazy val xpJuiceRatio = cfg.getInt("XPJuiceRatio")
    lazy val mobEssenceRatio = cfg.getInt("MobEssenceRatio")

    lazy val ratioMap = Map("mobessence" -> mobEssenceRatio, "essence" -> mobEssenceRatio, "xpjuice" -> xpJuiceRatio)
  }

}
