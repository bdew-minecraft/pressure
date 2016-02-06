/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computercraft

import dan200.computercraft.api.ComputerCraftAPI
import net.bdew.pressure.blocks.tank.blocks.{DataPortCommands, TileDataPort}
import net.bdew.pressure.blocks.valves.sensor.{PipeSensorCommands, TilePipeSensor}

object CCBlocks {
  def init(): Unit = {
    ComputerCraftAPI.registerPeripheralProvider(new TilePeripheralProvider("tank_dataport", DataPortCommands, classOf[TileDataPort]))
    ComputerCraftAPI.registerPeripheralProvider(new TilePeripheralProvider("pipe_sensor", PipeSensorCommands, classOf[TilePipeSensor]))
  }
}
