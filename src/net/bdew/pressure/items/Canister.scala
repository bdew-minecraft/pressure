/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items

import java.util

import net.bdew.lib.Misc
import net.bdew.lib.block.BlockRef
import net.bdew.lib.items.SimpleItem
import net.bdew.pressure.Pressure
import net.bdew.pressure.config.Tuning
import net.bdew.pressure.misc.PressureCreativeTabs
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids._

object Canister extends SimpleItem("Canister") with IFluidContainerItem {
  lazy val cfg = Tuning.getSection("Items").getSection(name)
  lazy val maxPour = cfg.getInt("MaxPour")
  lazy val capacity = cfg.getInt("Capacity")

  setMaxStackSize(1)

  override def getCreativeTabs = Array(PressureCreativeTabs.main, PressureCreativeTabs.canisters)

  override def getSubItems(item: Item, tab: CreativeTabs, jStacks: util.List[_]) {
    import scala.collection.JavaConversions._
    val stacks = jStacks.asInstanceOf[java.util.List[ItemStack]]
    if (tab == PressureCreativeTabs.main)
      stacks.add(new ItemStack(this))
    else if (tab == PressureCreativeTabs.canisters || tab == null) {
      stacks.addAll(
        FluidRegistry.getRegisteredFluids flatMap { case (id, fluid) =>
          if (FluidRegistry.getFluid(fluid.getName) != fluid) {
            Pressure.logError("Fluid %s is not registered correctly (%s <=> %s)", id, fluid, FluidRegistry.getFluid(fluid.getName))
            None
          } else {
            val tag = new NBTTagCompound
            val fStack = new FluidStack(fluid, capacity)
            fStack.writeToNBT(tag)
            val item = new ItemStack(this)
            item.setTagCompound(tag)
            Some(item)
          }
        }
      )
      if (tab == null) stacks.add(new ItemStack(this)) // for NEI
    }
  }

  def getFluid(stack: ItemStack): FluidStack = FluidStack.loadFluidStackFromNBT(stack.getTagCompound)

  def drain(stack: ItemStack, max: Int, doDrain: Boolean): FluidStack = {
    val fl = getFluid(stack)
    if (fl == null) return null
    val ns = new FluidStack(fl.fluidID, Misc.clamp(fl.amount, 0, max))
    if (doDrain) {
      fl.amount -= ns.amount
      val nbt = new NBTTagCompound()
      if (fl.amount > 0)
        fl.writeToNBT(nbt)
      stack.setTagCompound(nbt)
    }
    return ns
  }

  def fill(stack: ItemStack, fl: FluidStack, doFill: Boolean): Int = {
    val currStack = getFluid(stack)
    if (fl == null || (currStack != null && !currStack.isFluidEqual(fl))) return 0
    val current = if (currStack == null) 0 else currStack.amount
    val toFill = Misc.clamp(fl.amount, 0, capacity - current)
    if (doFill) {
      val newStack = new FluidStack(fl.getFluid, toFill + current)
      val nbt = new NBTTagCompound()
      newStack.writeToNBT(nbt)
      stack.setTagCompound(nbt)
    }
    return toFill
  }

  def getCapacity(container: ItemStack): Int = capacity

  override def addInformation(stack: ItemStack, player: EntityPlayer, lst: util.List[_], par4: Boolean) {
    import scala.collection.JavaConverters._
    val l = lst.asInstanceOf[util.List[String]].asScala
    val fl = getFluid(stack)
    if (fl == null)
      l += Misc.toLocal("bdlib.label.empty")
    else
      l += "%d/%d %s".format(fl.amount, capacity, fl.getFluid.getLocalizedName(fl))
  }

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (world.isRemote) return true
    val te = world.getTileEntity(x, y, z)
    if (te != null && te.isInstanceOf[IFluidHandler]) {
      val fh = te.asInstanceOf[IFluidHandler]
      val fl = drain(stack, maxPour, false)
      if (fl == null) return false
      val dir = ForgeDirection.values()(side)
      val toFill = fh.fill(dir, fl, false)
      if (toFill > 0) {
        fh.fill(dir, drain(stack, toFill, true), true)
        player.swingItem()
        return true
      }
    } else {
      val p = BlockRef(x, y, z).neighbour(Misc.forgeDirection(side))
      if (p.y >= 0 && p.y < world.getActualHeight && world.isAirBlock(p.x, p.y, p.z)) {
        val fs = getFluid(stack)
        if (fs != null && fs.getFluid != null && fs.getFluid.canBePlacedInWorld && fs.amount >= FluidContainerRegistry.BUCKET_VOLUME) {
          drain(stack, FluidContainerRegistry.BUCKET_VOLUME, true)
          world.setBlock(p.x, p.y, p.z, fs.getFluid.getBlock, 0, 3)
          world.notifyBlockOfNeighborChange(p.x, p.y, p.z, fs.getFluid.getBlock)
        }
      }
    }

    return false
  }
}
