/*
 * Copyright (c) bdew, 2013 - 2016
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

case class CallContext[T <: TileEntity](tile: T, computer: IComputerAccess, computers: Set[IComputerAccess], context: ILuaContext, rawParams: Array[AnyRef]) {
  private def optionalize(a: Array[AnyRef], n: Int) =
    (for (i <- 0 until Math.max(a.length, n)) yield {
      if (i >= a.length)
        None
      else
        Option(a(i))
    }).toArray

  def params[P1](p1: CCParam[P1]) =
    optionalize(rawParams, 1) match {
      case Array(p1(r1)) => r1
      case _ => throw new ParameterErrorException(p1)
    }

  def params[P1, P2](p1: CCParam[P1], p2: CCParam[P2]) =
    optionalize(rawParams, 2) match {
      case Array(p1(r1), p2(r2)) => (r1, r2)
      case _ => throw new ParameterErrorException(p1, p2)
    }

  // TODO: Add versions for more parameters
}

case class ParameterErrorException(params: CCParam[_]*) extends Exception("Expected parameters: " + params.map(_.name).mkString(", "))
