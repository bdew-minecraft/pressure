/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computercraft

import dan200.computercraft.api.lua.ILuaContext
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.tileentity.TileEntity

case class CallContext[T <: TileEntity](tile: T, computer: IComputerAccess, computers: Set[IComputerAccess], context: ILuaContext, params: Array[AnyRef])