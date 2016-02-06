/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computercraft

import dan200.computercraft.api.lua.LuaException
import net.bdew.lib.async.Async
import net.minecraft.tileentity.TileEntity

import scala.concurrent.Future


class TileCommandHandler[T <: TileEntity] {
  var commands = Map.empty[String, CallContext[T] => Future[CCResult]]

  private def wrapInFuture(f: CallContext[T] => CCResult): CallContext[T] => Future[CCResult] =
    (x: CallContext[T]) => Async.inServerThread(f(x))

  def async(name: String)(f: CallContext[T] => Future[CCResult]) = commands += (name -> f)
  def command(name: String)(f: CallContext[T] => CCResult) = commands += (name -> wrapInFuture(f))

  def err(err: String) = throw new LuaException(err)

  lazy val commandNames = commands.keys.toArray
  lazy val idToCommand = (commandNames.zipWithIndex map (_.swap)).toMap
}
