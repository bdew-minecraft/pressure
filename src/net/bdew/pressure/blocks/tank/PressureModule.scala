/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import net.bdew.lib.multiblock.tile.{TileController, TileModule}
import net.bdew.pressure.api.IPressureTile
import net.bdew.pressure.pressurenet.Helper

trait PressureModule extends TileModule with IPressureTile {
  override def connect(target: TileController) {
    super.connect(target)
    Helper.notifyBlockChanged(getWorld, getPos)
  }

  override def coreRemoved() {
    Helper.notifyBlockChanged(getWorld, getPos)
    super.coreRemoved()
  }
}
