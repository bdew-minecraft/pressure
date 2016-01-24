/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.misc

import java.io._

import net.bdew.lib.data.DataSlotTankBase
import net.bdew.lib.data.base.{DataSlot, DataSlotContainer, UpdateKind}
import net.bdew.pressure.compat.computers.Result
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.{FluidStack, FluidRegistry, Fluid}

import scala.collection.mutable

case class DataSlotFluidCounts(name: String, parent: DataSlotContainer, size: Int) extends DataSlot {
  val values = mutable.HashMap[Fluid, Int]()

  def reset() {
    values.clear()
  }

  def update(f: Fluid, count: Int) = {
    val newValue = values.get(f).flatMap((prev: Int) => {
      try {
        Some(Math.addExact(prev, count))
      } catch {
        case _: ArithmeticException => None
      }
    }).getOrElse(count)
    values += f -> newValue
  }

  override def save(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    if (values.isEmpty) return
    val baos = new ByteArrayOutputStream
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(values.iterator.map({
      case (fluid, count) => fluid.getName -> count
    }).toMap)
    oos.close()
    t.setByteArray(name, baos.toByteArray)
  }

  override def load(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    reset()
    val bytes = t.getByteArray(name)
    if (bytes.isEmpty) return
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    ois.readObject() match {
      case fluids: Map[_, _] => fluids.foreach({
        case (fluidName: String, count: Int) if FluidRegistry.isFluidRegistered(fluidName) =>
          values += FluidRegistry.getFluid(fluidName) -> count
      })
    }
    ois.close()
  }
}

object FluidMapHelpers {
  def fluidPairsToResult[R](it: Iterator[(Fluid, R)], valueName: String)(implicit ev: R => Result): Result = {
    it.map ({
      case (fluid, value) => Result.Map (
        "name" -> fluid.getName,
        valueName -> value
      )
    }).toList
  }
}

trait CountedDataSlotTank extends DataSlotTankBase { wrapped: DataSlotTankBase =>
  val fluidIn = DataSlotFluidCounts(wrapped.name + ":fluidIn", wrapped.parent, 50).setUpdate(UpdateKind.SAVE)
  val fluidOut = DataSlotFluidCounts(wrapped.name + ":fluidOut", wrapped.parent, 50).setUpdate(UpdateKind.SAVE)

  override def fill(resource: FluidStack, doFill: Boolean) = {
    val ret = super.fill(resource, doFill)
    if (doFill) {
      fluidIn.update(resource.getFluid, ret)
    }
    ret
  }

  override def drain(maxDrain: Int, doDrain: Boolean) = {
    val ret = super.drain(maxDrain, doDrain)
    if (doDrain) {
      fluidOut.update(ret.getFluid, ret.amount)
    }
    ret
  }
}
