/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computercraft

import dan200.computercraft.api.peripheral.IPeripheralProvider
import net.bdew.lib.computers.TileCommandHandler
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TilePeripheralProvider[T <: TileEntity](kind: String, commands: TileCommandHandler[T], teClass: Class[T]) extends IPeripheralProvider {
  override def getPeripheral(world: World, pos: BlockPos, side: EnumFacing) = {
    val te = world.getTileEntity(pos)
    if (te != null && teClass.isInstance(te))
      TilePeripheralWrapper(kind, commands, te.asInstanceOf[T])
    else
      null
  }
}
