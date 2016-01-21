/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computercraft

import dan200.computercraft.api.lua.{LuaException, ILuaContext, ILuaObject}
import li.cil.oc.api.machine.{Arguments, Context}
import li.cil.oc.api.network.ManagedPeripheral
import li.cil.oc.api.prefab.AbstractValue
import net.bdew.pressure.compat.computercraft.CCResult.ResArray
import net.minecraft.nbt.NBTTagCompound

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.util.{Failure, Success}


abstract class PersistableFutureState {
  def checkPending: PersistableFutureState
  def isCompleted: Boolean
  def value: CCResult
}

case class Pending(future: Future[CCResult]) extends PersistableFutureState {
  def checkPending = future.value match {
    case Some(Success(v)) => Succeeded(v)
    case Some(Failure(t)) => Failed(t.getMessage)
    case _ => this
  }
  def isCompleted = false
  def value = throw new LuaException("future not completed")
}

case class Succeeded(result: CCResult) extends PersistableFutureState {
  def checkPending = this
  def isCompleted = true
  def value = result
}

case class Failed(message: String) extends PersistableFutureState {
  def checkPending = this
  def isCompleted = true
  def value = ResArray(Array(CCResult.Nil, message))
}

class OpaqueLuaFutureWrapper(var value: PersistableFutureState) extends ILuaObject {
  def this(future: Future[CCResult]) {
    this(Pending(future))
  }

  val lua_methods = new ArrayBuffer[(String, (Array[AnyRef]) => CCResult)]

  def checkPending() = {
    value = value.checkPending
  }

  lua_methods.append("isCompleted" -> { _ =>
    checkPending()
    value.isCompleted
  })
  lua_methods.append("value" -> { _ =>
    checkPending()
    value.value
  })

  override def getMethodNames: Array[String] = lua_methods.iterator.map(_._1).toArray

  override def callMethod(context: ILuaContext, method: Int, arguments: Array[AnyRef]): Array[AnyRef] = {
    lua_methods.apply(method)._2(arguments).wrap
  }
}

class ManagedPeripheralWrapper(var wrapper: OpaqueLuaFutureWrapper) extends AbstractValue with ManagedPeripheral with ILuaObject {
  def this() {
    this(new OpaqueLuaFutureWrapper(Failed("persisted while pending")))
  }

  override def getMethodNames = wrapper.getMethodNames
  override def methods = wrapper.getMethodNames

  override def callMethod(context: ILuaContext, method: Int, arguments: Array[AnyRef]) =
    wrapper.callMethod(context, method, arguments)
  override def invoke(method: String, context: Context, args: Arguments): Array[Object] = {
    wrapper.lua_methods.find(_._1 == method) match {
      case Some(t) => t._2(args.toArray).wrap
      case None => throw new LuaException("no method " + method)
    }
  }
}
