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
import net.bdew.lib.async.ServerTickExecutionContext

import scala.concurrent.Future
import scala.util.Try

trait ExecutionHelper {
  def canWorkWith(ctx: ILuaContext): Boolean
  def waitForFuture[T](ctx: ILuaContext, comp: IComputerAccess, future: Future[T]): Try[T]
}

object DefaultExecutionHelper extends ExecutionHelper {
  override def canWorkWith(ctx: ILuaContext): Boolean = true
  override def waitForFuture[T](ctx: ILuaContext, comp: IComputerAccess, future: Future[T]): Try[T] = {
    future.onComplete(x => comp.queueEvent("bdew.wakeup", Array.empty))(ServerTickExecutionContext)
    while (!future.isCompleted) {
      ctx.pullEventRaw("bdew.wakeup")
    }
    future.value.get
  }
}

object ExecutionHelpers {
  var registry = List.empty[ExecutionHelper]
  def waitForFuture[T](ctx: ILuaContext, comp: IComputerAccess, future: Future[T]): Try[T] = {
    for (helper <- registry)
      if (helper.canWorkWith(ctx))
        return helper.waitForFuture(ctx, comp, future)
    DefaultExecutionHelper.waitForFuture(ctx, comp, future)
  }
}