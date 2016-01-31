/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure

import net.bdew.pressure.blocks.tank.blocks.TileTankIndicator
import net.bdew.pressure.model.{ExtendedModelLoader, TankIndicatorTESR}
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.fml.client.registry.ClientRegistry

object PressureClient {
  def preInit(): Unit = {
    ModelLoaderRegistry.registerLoader(ExtendedModelLoader)
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileTankIndicator], TankIndicatorTESR)
    sensor.Icons.init()
  }
}
