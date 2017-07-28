/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.opencomputers

import li.cil.oc.api.Driver
import net.bdew.pressure.blocks.tank.blocks.{DataPortCommands, TileDataPort}
import net.bdew.pressure.blocks.valves.sensor.{PipeSensorCommands, TilePipeSensor}

object OCBlocks {
  def init(): Unit = {
    Driver.add(new BlockDriver("pp_tank", DataPortCommands, classOf[TileDataPort]))
    Driver.add(new BlockDriver("pp_sensor", PipeSensorCommands, classOf[TilePipeSensor]))
  }
}
