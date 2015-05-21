/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computercraft

import dan200.computercraft.api.peripheral.{IPeripheral, IPeripheralProvider}
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class TilePeripheralProvider[T <: TileEntity](kind: String, commands: TileCommandHandler[T], teClass: Class[T]) extends IPeripheralProvider {
  override def getPeripheral(world: World, x: Int, y: Int, z: Int, side: Int): IPeripheral = {
    val te = world.getTileEntity(x, y, z)
    if (te != null && teClass.isInstance(te))
      TilePeripheralWrapper(kind, commands, te.asInstanceOf[T])
    else
      null
  }
}
