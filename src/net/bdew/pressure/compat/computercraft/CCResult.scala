/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.computercraft

import scala.collection.JavaConverters._

sealed trait CCResult {
  def encode: AnyRef
  def wrap = Array(encode)
}

object CCResult {

  import scala.language.implicitConversions

  case class ResInt(v: Int) extends CCResult {
    override def encode = Int.box(v)
  }

  case class ResFloat(v: Float) extends CCResult {
    override def encode = Float.box(v)
  }

  case class ResDouble(v: Double) extends CCResult {
    override def encode = Double.box(v)
  }

  case class ResBoolean(v: Boolean) extends CCResult {
    override def encode = Boolean.box(v)
  }

  case class ResString(v: String) extends CCResult {
    override def encode = v
  }

  case class ResArray(v: Array[CCResult]) extends CCResult {
    override def encode = wrap
    override def wrap = v.map(_.encode)
  }

  case class ResMap(v: Map[String, CCResult]) extends CCResult {
    override def encode = v.mapValues(_.encode).asJava
  }

  case class ResList(v: List[CCResult]) extends CCResult {
    override def encode = v.zipWithIndex.map({ case (vv, kk) => kk + 1 -> vv.encode }).toMap.asJava
  }

  object Null extends CCResult {
    override def encode = wrap
    override def wrap = Array(null)
  }

  def apply(v: CCResult) = v

  def List(vals: CCResult*): CCResult = vals.toList
  def Map(vals: (String, CCResult)*): CCResult = vals.toMap

  implicit def resInt(v: Int): CCResult = ResInt(v)
  implicit def resFloat(v: Float): CCResult = ResFloat(v)
  implicit def resDouble(v: Double): CCResult = ResDouble(v)
  implicit def resBoolean(v: Boolean): CCResult = ResBoolean(v)
  implicit def resString(v: String): CCResult = ResString(v)
  implicit def resMap(v: Map[String, CCResult]): CCResult = ResMap(v)
  implicit def resList(v: List[CCResult]): CCResult = ResList(v)
}