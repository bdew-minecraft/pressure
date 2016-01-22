/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computercraft

import java.io.{ByteArrayInputStream, ObjectInputStream, ObjectOutputStream, ByteArrayOutputStream}

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
  def serialize: Option[AnyRef]
}

case class Pending(future: Future[CCResult]) extends PersistableFutureState {
  def checkPending = future.value match {
    case Some(Success(v)) => Succeeded(v)
    case Some(Failure(t)) => Failed(t.getMessage)
    case _ => this
  }
  def isCompleted = false
  def value = throw new LuaException("future not completed")
  def serialize = None
}

case class Succeeded(result: CCResult) extends PersistableFutureState {
  def checkPending = this
  def isCompleted = true
  def value = result
  def serialize = Some(("success", result))
}

case class Failed(message: String) extends PersistableFutureState {
  def checkPending = this
  def isCompleted = true
  def value = ResArray(Array(CCResult.Nil, message))
  def serialize = Some(("failure", message))
}

object PersistableFutureState {
  def apply(serialized: AnyRef): Option[PersistableFutureState] = {
    serialized match {
      case ("success", v: CCResult) => Some(Succeeded(v))
      case ("failure", f: String) => Some(Failed(f))
      case _ => None
    }
  }
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

  val tagName = "serialized"

  override def save(t: NBTTagCompound): Unit = {
    val obj = wrapper.value.serialize match {
      case None => return
      case Some(v) => v
    }
    val baos = new ByteArrayOutputStream
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(obj)
    oos.close()
    t.setByteArray(tagName, baos.toByteArray)
  }

  override def load(t: NBTTagCompound): Unit = {
    if (!t.hasKey(tagName)) return
    val bytes = t.getByteArray(tagName)
    if (bytes.isEmpty) return
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    PersistableFutureState.apply(ois.readObject()) match {
      case None => ()
      case Some(v) => wrapper = new OpaqueLuaFutureWrapper(v)
    }
    ois.close()
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
