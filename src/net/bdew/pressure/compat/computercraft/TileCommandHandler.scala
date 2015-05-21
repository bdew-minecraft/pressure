/*
 * Copyright (c) bdew, 2013 - 2015
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
  var commands = Map.empty[String, CallContext[T] => Future[Array[AnyRef]]]

  private def wrapInFuture(f: CallContext[T] => Array[AnyRef]): CallContext[T] => Future[Array[AnyRef]] =
    (x: CallContext[T]) => Async.inServerThread(f(x))

  def async(name: String)(f: CallContext[T] => Future[Array[AnyRef]]) = commands += (name -> f)
  def command(name: String)(f: CallContext[T] => Array[AnyRef]) = commands += (name -> wrapInFuture(f))

  def res(v: Int): Array[AnyRef] = Array(Int.box(v))
  def res(v: Float): Array[AnyRef] = Array(Float.box(v))
  def res(v: Double): Array[AnyRef] = Array(Double.box(v))
  def res(v: Boolean): Array[AnyRef] = Array(Boolean.box(v))
  def res(v: String): Array[AnyRef] = Array(v)

  def err(err: String) = throw new LuaException(err)

  lazy val commandNames = commands.keys.toArray
  lazy val idToCommand = (commandNames.zipWithIndex map (_.swap)).toMap
}
