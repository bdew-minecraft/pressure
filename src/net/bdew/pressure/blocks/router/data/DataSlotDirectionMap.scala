/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router.data

import net.bdew.lib.data.base.{DataSlot, UpdateKind}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

abstract class DataSlotDirectionMap[T <: Enumeration](val enum: T, val default: T#Value) extends DataSlot {
  var map = EnumFacing.values().map(_ -> default).toMap

  def get(d: EnumFacing) = map(d)
  def set(d: EnumFacing, m: T#Value) = {
    map += d -> m
    parent.dataSlotChanged(this)
  }

  override def save(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    t.setByteArray(name, EnumFacing.values().map(map(_).id.toByte))
  }

  override def load(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    val loaded = t.getByteArray(name).zipWithIndex.map { case (v, n) => EnumFacing.values()(n) -> enum(v) }.toMap
    map = EnumFacing.values().map(x => x -> loaded.getOrElse(x, default)).toMap
  }
}






