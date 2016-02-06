/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.misc

import net.bdew.lib.computers.Result
import net.bdew.lib.data.DataSlotTankBase
import net.bdew.lib.data.base.{DataSlot, DataSlotContainer, UpdateKind}
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.{Fluid, FluidRegistry, FluidStack}

import scala.collection.mutable

case class DataSlotFluidCounts(name: String, parent: DataSlotContainer) extends DataSlot {
  val values = mutable.HashMap[Fluid, Int]()

  def reset() {
    values.clear()
  }

  def update(f: Fluid, count: Int) = {
    val oldVal = values.getOrElse(f, 0)
    values(f) = if (Int.MaxValue - count >= oldVal)
      count + oldVal
    else
      count // reset on overflow
  }

  override def save(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    if (values.isEmpty) return
    val map = new NBTTagCompound
    for ((fluid, value) <- values) {
      map.setInteger(fluid.getName, value)
    }
    t.setTag(name, map)
  }

  override def load(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    reset()
    import scala.collection.JavaConversions._
    if (t.hasKey(name)) {
      val map = t.getCompoundTag(name)
      for (name <- map.getKeySet if FluidRegistry.isFluidRegistered(name))
        values(FluidRegistry.getFluid(name)) = map.getInteger(name)
    }
  }
}

object FluidMapHelpers {
  def fluidPairsToResult[R](pairs: Traversable[(Fluid, R)], valueName: String)(implicit ev: R => Result): Result = {
    pairs.map({
      case (fluid, value) => Result.Map(
        "name" -> fluid.getName,
        valueName -> value
      )
    }).toList
  }
}

trait CountedDataSlotTank extends DataSlotTankBase {
  val fluidIn = DataSlotFluidCounts(name + ":fluidIn", parent).setUpdate(UpdateKind.SAVE)
  val fluidOut = DataSlotFluidCounts(name + ":fluidOut", parent).setUpdate(UpdateKind.SAVE)

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
