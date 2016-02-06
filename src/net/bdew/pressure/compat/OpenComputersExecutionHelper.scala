/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat

import dan200.computercraft.api.lua.{ILuaContext, LuaException}
import dan200.computercraft.api.peripheral.IComputerAccess
import net.bdew.lib.async.ServerTickExecutionContext
import net.bdew.pressure.compat.computercraft.ExecutionHelper
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side

import scala.concurrent.Future
import scala.util.Try

object OpenComputersExecutionHelper extends ExecutionHelper {
  override def canWorkWith(ctx: ILuaContext): Boolean =
    ctx.getClass.getName == "li.cil.oc.integration.computercraft.DriverPeripheral$Environment$UnsupportedLuaContext"

  override def waitForFuture[T](ctx: ILuaContext, comp: IComputerAccess, future: Future[T]): Try[T] = {
    // OpenComputers can't yield from peripheral code, hackery incoming
    if (FMLCommonHandler.instance().getEffectiveSide == Side.SERVER) {
      // This is the main server thread, lets try to run the execution context once to complete the future
      if (!future.isCompleted)
        ServerTickExecutionContext.doSingleLoop()

      // If it's still incomplete, it might be waiting for something to happen in the server thread
      // Which would lead to a deadlock and freeze the whole server. To avoid this we abort here
      if (!future.isCompleted)
        throw new LuaException("This method is not safe to call in server thread, use direct mode if possible")

      future.value.get

    } else {
      // In worker thread, just sleep until the future is completed, hopefully it will be fast enough
      while (!future.isCompleted) {
        Thread.sleep(10)
      }
      future.value.get
    }
  }
}
