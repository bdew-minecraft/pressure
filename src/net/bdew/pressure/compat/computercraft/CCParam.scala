/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computercraft

sealed trait CCParam[R] {
  def name: String
  def unapply(v: Option[Any]): Option[R]
}

case class CCSimpleParam[R, T](name: String, runtimeClass: Class[R]) extends CCParam[R] {
  override def unapply(v: Option[Any]): Option[R] = v match {
    case Some(vv) if runtimeClass.isInstance(vv) => Some(vv.asInstanceOf[R])
    case _ => None
  }
}

object CCString extends CCSimpleParam("string", classOf[String])

// Need to use the java (boxed) versions
object CCNumber extends CCSimpleParam("number", classOf[java.lang.Double])

object CCBoolean extends CCSimpleParam("boolean", classOf[java.lang.Boolean])

case class CCOption[T](p: CCParam[T]) extends CCParam[Option[T]] {
  override def name = "[%s]".format(p.name)
  override def unapply(v: Option[Any]) = v match {
    case None => Some(None)
    case p(vv) => Some(Some(vv))
    case _ => None
  }
}
