/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.blocks.tank.BaseModule

object BlockTankBlock extends BaseModule("tank_block", "TankBlock", classOf[TileTankBlock])

class TileTankBlock extends TileModule {
  val kind: String = "TankBlock"
}
