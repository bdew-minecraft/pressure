/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items

import java.util

import net.bdew.lib.Misc
import net.bdew.lib.items.BaseItem
import net.bdew.pressure.Pressure
import net.bdew.pressure.config.{Config, Tuning}
import net.bdew.pressure.misc.PressureCreativeTabs
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fluids._
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object Canister extends BaseItem("Canister") with IFluidContainerItem {
  lazy val cfg = Tuning.getSection("Items").getSection(name)
  lazy val maxPour = cfg.getInt("MaxPour")
  lazy val capacity = cfg.getInt("Capacity")

  setMaxStackSize(1)
  setHasSubtypes(true)

  override def getCreativeTabs = Array(PressureCreativeTabs.main, PressureCreativeTabs.canisters)

  override def getSubItems(item: Item, tab: CreativeTabs, stacks: util.List[ItemStack]) = {
    import scala.collection.JavaConversions._
    if (tab == PressureCreativeTabs.main)
      stacks.add(new ItemStack(this))
    else if (tab == PressureCreativeTabs.canisters || tab == null) {
      if (tab == PressureCreativeTabs.canisters || Config.showCanisters) {
        stacks.addAll(
          FluidRegistry.getRegisteredFluids flatMap { case (id, fluid) =>
            if (FluidRegistry.getFluid(fluid.getName) != fluid) {
              Pressure.logError("Fluid %s is not registered correctly (%s <=> %s)", id, fluid, FluidRegistry.getFluid(fluid.getName))
              None
            } else if (!FluidRegistry.isFluidRegistered(fluid)) {
              Pressure.logError("Forge claims fluid '%s' is not registered after returning it from getRegisteredFluids", fluid.getName)
              None
            } else {
              try {
                val tag = new NBTTagCompound
                val fStack = new FluidStack(fluid, capacity)
                fStack.writeToNBT(tag)
                val item = new ItemStack(this)
                item.setTagCompound(tag)
                Some(item)
              } catch {
                case e: Throwable =>
                  Pressure.logErrorException("Exception while creating canister for fluid '%s'", e, fluid.getName)
                  None
              }
            }
          }
        )
      }
      if (tab == null) stacks.add(new ItemStack(this)) // for NEI
    }
  }

  def getFluid(stack: ItemStack): FluidStack = FluidStack.loadFluidStackFromNBT(stack.getTagCompound)

  def drain(stack: ItemStack, max: Int, doDrain: Boolean): FluidStack = {
    val fl = getFluid(stack)
    if (fl == null) return null
    val ns = new FluidStack(fl, Misc.clamp(fl.amount, 0, max))
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

  override def addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: util.List[String], advanced: Boolean) = {
    val fl = getFluid(stack)
    if (fl == null) {
      tooltip.add(Misc.toLocal("bdlib.label.empty"))
    } else {
      tooltip.add("%d/%d %s".format(fl.amount, capacity, fl.getFluid.getLocalizedName(fl)))
      if (Config.showFluidName) tooltip.add("ID: " + fl.getFluid.getName)
    }
  }

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    if (world.isRemote) return EnumActionResult.SUCCESS
    val te = world.getTileEntity(pos)
    if (te != null && te.isInstanceOf[IFluidHandler]) {
      val fh = te.asInstanceOf[IFluidHandler]
      val fl = drain(stack, maxPour, false)
      if (fl == null) return EnumActionResult.FAIL
      val toFill = fh.fill(side, fl, false)
      if (toFill > 0) {
        fh.fill(side, drain(stack, toFill, true), true)
        player.swingArm(hand)
        return EnumActionResult.SUCCESS
      }
    } else {
      val p = pos.offset(side)
      if (p.getY >= 0 && p.getY < world.getActualHeight && world.isAirBlock(p)) {
        val fs = getFluid(stack)
        if (fs != null && fs.getFluid != null && fs.getFluid.canBePlacedInWorld && fs.amount >= FluidContainerRegistry.BUCKET_VOLUME) {
          drain(stack, FluidContainerRegistry.BUCKET_VOLUME, true)
          world.setBlockState(p, fs.getFluid.getBlock.getDefaultState, 3)
          world.notifyBlockOfStateChange(p, fs.getFluid.getBlock)
        }
      }
    }

    return EnumActionResult.FAIL
  }

  @SideOnly(Side.CLIENT)
  override def registerItemModels() = {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("pressure:canister.extended", "inventory"))
  }
}
