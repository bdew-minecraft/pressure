/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.config

import net.bdew.lib.config.{MachineManager, MachineManagerMultiblock}
import net.bdew.pressure.blocks.router.MachineRouter
import net.bdew.pressure.blocks.tank.MachineTank
import net.bdew.pressure.misc.PressureCreativeTabs

object Machines extends MachineManager(Tuning.getSection("Machines"), Config.guiHandler, PressureCreativeTabs.main) with MachineManagerMultiblock {
  registerMachine(MachineTank)
  registerMachine(MachineRouter)
}
