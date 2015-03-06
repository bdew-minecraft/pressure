/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router.data

import net.bdew.lib.data.base.{DataSlot, UpdateKind}
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.ForgeDirection

abstract class DataSlotDirectionMap[T <: Enumeration](val enum: T, val default: T#Value) extends DataSlot {
  var map = ForgeDirection.VALID_DIRECTIONS.map(_ -> default).toMap

  def get(d: ForgeDirection) = map(d)
  def set(d: ForgeDirection, m: T#Value) = {
    map += d -> m
    parent.dataSlotChanged(this)
  }

  override def save(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    t.setByteArray(name, ForgeDirection.VALID_DIRECTIONS.map(map(_).id.toByte))
  }

  override def load(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    val loaded = t.getByteArray(name).zipWithIndex.map { case (v, n) => ForgeDirection.values()(n) -> enum(v) }.toMap
    map = ForgeDirection.VALID_DIRECTIONS.map(x => x -> loaded.getOrElse(x, default)).toMap
  }
}






