/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computercraft

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.Side
import dan200.computercraft.api.lua.{ILuaContext, LuaException}
import dan200.computercraft.api.peripheral.{IComputerAccess, IPeripheral}
import li.cil.oc.api.machine.LimitReachedException
import net.bdew.lib.async.ServerTickExecutionContext
import net.minecraft.tileentity.TileEntity

import scala.util.{Failure, Success}

case class TilePeripheralWrapper[T <: TileEntity](kind: String, commands: TileCommandHandler[T], tile: T) extends IPeripheral {
  var computers = Set.empty[IComputerAccess]

  override def getType: String = kind

  override def detach(computer: IComputerAccess): Unit =
    if (computers.contains(computer))
      computers -= computer

  override def attach(computer: IComputerAccess): Unit =
    computers += computer

  override def equals(other: IPeripheral): Boolean =
    other match {
      case x: TilePeripheralWrapper[T] => x.tile == tile && x.commands == commands & x.kind == kind
      case _ => false
    }

  override def getMethodNames: Array[String] = commands.commandNames

  override def callMethod(computer: IComputerAccess, context: ILuaContext, method: Int, arguments: Array[AnyRef]): Array[AnyRef] = {
    val handler = commands.commands(commands.idToCommand(method))
    val ctx = CallContext(tile, computer, computers, context, arguments)
    val future = handler(ctx)

    if (context.getClass.getSimpleName == "UnsupportedLuaContext") {
      // Runnning in OpenComputers which can't yield. Hackery incoming.
      if (FMLCommonHandler.instance().getEffectiveSide == Side.SERVER) {
        // We are in main server thread, let the future run by directly looping the execution context
        while (!future.isCompleted) {
          ServerTickExecutionContext.doSingleLoop()
        }
      } else {
        // We are in some other thread, barf and let OC re-run us in server
        throw new LimitReachedException()
      }
    } else {
      // Running in ComputerCraft, business as usual
      future.onComplete(x => computer.queueEvent("bdew.wakeup", Array.empty))(ServerTickExecutionContext)
      while (!future.isCompleted) {
        context.pullEventRaw("bdew.wakeup")
      }
    }

    future.value match {
      case Some(Success(null)) => null
      case Some(Success(v)) => v.wrap
      case Some(Failure(e: ParameterErrorException)) =>
        throw new LuaException("Usage: %s(%s)".format(commands.idToCommand(method), e.params.map(_.name).mkString(", ")))
      case Some(Failure(t)) => throw t
      case None => sys.error("Future not resolved after waiting, this is a bug")
    }
  }
}
