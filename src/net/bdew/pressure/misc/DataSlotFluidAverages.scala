/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.misc

import java.io._

import net.bdew.lib.data.base.{DataSlot, DataSlotContainer, UpdateKind}
import net.bdew.pressure.Pressure
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.{Fluid, FluidRegistry}

import scala.collection.mutable

case class DataSlotFluidAverages(name: String, parent: DataSlotContainer, size: Int) extends DataSlot {
  val values = mutable.Queue.empty[Map[Fluid, Double]]

  def reset() {
    values.clear()
  }

  def update(v: Map[Fluid, Double]) = {
    values += v
    if (values.length > size)
      values.dequeue()
    parent.dataSlotChanged(this)
  }

  def getAverages = DataSlotFluidAverages.getAverages(values)

  override def save(t: NBTTagCompound, kind: UpdateKind.Value) = {
    if (values.nonEmpty)
      t.setByteArray(name, DataSlotFluidAverages.serializeAverages(values))
  }

  override def load(t: NBTTagCompound, kind: UpdateKind.Value) = {
    reset()
    val bytes = t.getByteArray(name)
    if (bytes.nonEmpty) {
      values ++= DataSlotFluidAverages.unSerializeAverages(bytes)
    }
  }
}

object DataSlotFluidAverages {
  def serializeAverages(values: Seq[Map[Fluid, Double]]) = {
    val stream = new ByteArrayOutputStream()
    val output = new DataOutputStream(stream)
    output.writeInt(values.size)
    for (map <- values) {
      output.writeInt(map.size)
      for ((fluid, amount) <- map) {
        output.writeUTF(fluid.getName)
        output.writeDouble(amount)
      }
    }
    stream.toByteArray
  }

  def unSerializeAverages(bytes: Array[Byte]) = {
    try {
      val stream = new ByteArrayInputStream(bytes)
      val input = new DataInputStream(stream)
      val queueSize = input.readInt()
      for (i <- 0 until queueSize) yield {
        val mapSize = input.readInt()
        val map = for (j <- 0 until mapSize) yield {
          val name = input.readUTF()
          val amount = input.readDouble()
          if (FluidRegistry.isFluidRegistered(name))
            Some(FluidRegistry.getFluid(name) -> amount)
          else
            None
        }
        map.flatten.toMap
      }
    } catch {
      case e: Throwable =>
        Pressure.logErrorException("Failed loading FluidAverages data", e)
        List.empty
    }
  }

  def getAverages(values: Seq[Map[Fluid, Double]]): Map[Fluid, Double] = {
    val sums = mutable.Map.empty[Fluid, Double].withDefaultValue(0)
    val len = values.size
    if (len > 0) {
      for {
        valueMap <- values
        (fluid, value) <- valueMap
      } {
        sums(fluid) += value
      }
      sums.toMap.mapValues(_ / len)
    } else Map.empty
  }
}
