/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.misc

import net.bdew.lib.CreativeTabContainer
import net.bdew.pressure.config.Items
import net.bdew.pressure.items.Canister

object PressureCreativeTabs extends CreativeTabContainer {
  val main = new Tab("bdew.pressure", Items.interface)
  val canisters = new Tab("bdew.canisters", Canister)
}
