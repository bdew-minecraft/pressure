/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items

import java.util

import net.bdew.lib.Misc
import net.bdew.lib.capabilities.helpers.FluidHelper
import net.bdew.lib.capabilities.{Capabilities, CapabilityProviderItem}
import net.bdew.lib.items.BaseItem
import net.bdew.pressure.Pressure
import net.bdew.pressure.config.{Config, Tuning}
import net.bdew.pressure.misc.PressureCreativeTabs
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand, NonNullList}
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fluids.capability.{IFluidHandlerItem, IFluidTankProperties}
import net.minecraftforge.fluids.{Fluid, FluidRegistry, FluidStack}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object Canister extends BaseItem("canister") with CapabilityProviderItem {
  lazy val cfg = Tuning.getSection("Items").getSection("Canister")
  lazy val maxPour = cfg.getInt("MaxPour")
  lazy val capacity = cfg.getInt("Capacity")

  setMaxStackSize(1)
  setHasSubtypes(true)

  addCapability(Capabilities.CAP_FLUID_HANDLER_ITEM, FluidHandler)

  override def getCreativeTabs = Array(PressureCreativeTabs.main, PressureCreativeTabs.canisters)

  override def getSubItems(tab: CreativeTabs, stacks: NonNullList[ItemStack]): Unit = {
    import scala.collection.JavaConversions._
    if (tab == PressureCreativeTabs.main || tab == CreativeTabs.SEARCH)
      stacks.add(new ItemStack(this))
    else if (tab == PressureCreativeTabs.canisters || tab == CreativeTabs.SEARCH || tab == null) {
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

  def getContainedFluid(stack: ItemStack): FluidStack = FluidStack.loadFluidStackFromNBT(stack.getTagCompound)

  case class FluidHandler(stack: ItemStack) extends IFluidHandlerItem with IFluidTankProperties {
    override def canFill: Boolean = true
    override def canDrain: Boolean = true
    override def canFillFluidType(fluidStck: FluidStack): Boolean = true
    override def canDrainFluidType(fluidStack: FluidStack): Boolean = true
    override def getContents: FluidStack = getContainedFluid(stack)
    override def getCapacity: Int = capacity
    override def getContainer: ItemStack = stack

    def setFluid(fluid: FluidStack): Unit = {
      val nbt = new NBTTagCompound()
      fluid.writeToNBT(nbt)
      stack.setTagCompound(nbt)
    }

    override def getTankProperties: Array[IFluidTankProperties] = Array(this)

    override def fill(resource: FluidStack, doFill: Boolean): Int = {
      if (stack.getCount != 1 || resource == null || resource.amount <= 0) return 0
      val contained = getContents
      if (contained == null) {
        val fillAmount = Math.min(capacity, resource.amount)
        if (doFill) {
          val filled = resource.copy
          filled.amount = fillAmount
          setFluid(filled)
        }
        fillAmount
      } else {
        if (contained.isFluidEqual(resource)) {
          val fillAmount = Math.min(capacity - contained.amount, resource.amount)
          if (doFill && fillAmount > 0) {
            contained.amount += fillAmount
            setFluid(contained)
          }
          fillAmount
        } else 0
      }
    }

    override def drain(resource: FluidStack, doDrain: Boolean): FluidStack = {
      if (stack.getCount != 1 || resource == null || resource.amount <= 0 || !resource.isFluidEqual(getContents))
        null
      else
        drain(resource.amount, doDrain)
    }

    override def drain(maxDrain: Int, doDrain: Boolean): FluidStack = {
      if (stack.getCount != 1 || maxDrain <= 0) return null
      val contained = getContents
      if (contained == null || contained.amount <= 0) return null
      val drainAmount = Math.min(contained.amount, maxDrain)
      val drained = contained.copy
      drained.amount = drainAmount
      if (doDrain) {
        contained.amount -= drainAmount
        if (contained.amount == 0) {
          stack.setTagCompound(null)
        } else {
          setFluid(contained)
        }
      }
      drained
    }
  }

  override def addInformation(stack: ItemStack, world: World, tooltip: util.List[String], flagIn: ITooltipFlag): Unit = {
    val fl = getContainedFluid(stack)
    if (fl == null) {
      tooltip.add(Misc.toLocal("bdlib.label.empty"))
    } else {
      tooltip.add("%d/%d %s".format(fl.amount, capacity, fl.getFluid.getLocalizedName(fl)))
      if (Config.showFluidName) tooltip.add("ID: " + fl.getFluid.getName)
    }
  }

  override def onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    val stack = player.getHeldItem(hand)
    if (world.isRemote) return EnumActionResult.SUCCESS
    val me = stack.getCapability(Capabilities.CAP_FLUID_HANDLER_ITEM, null)
    FluidHelper.getFluidHandler(world, pos, side) foreach { target =>
      val filled = FluidHelper.pushFluid(me, target, true, maxPour)
      if (filled != null) {
        player.swingArm(hand)
        return EnumActionResult.SUCCESS
      }
    }
    val p = pos.offset(side)
    if (p.getY >= 0 && p.getY < world.getActualHeight && world.isAirBlock(p)) {
      val fs = me.drain(Fluid.BUCKET_VOLUME, false)
      if (fs != null && fs.getFluid != null && fs.getFluid.canBePlacedInWorld && fs.amount == Fluid.BUCKET_VOLUME) {
        me.drain(fs, true)
        world.setBlockState(p, fs.getFluid.getBlock.getDefaultState, 3)
        world.neighborChanged(p, fs.getFluid.getBlock, p)
        return EnumActionResult.SUCCESS
      }
    }
    EnumActionResult.FAIL
  }

  override def getItemStackDisplayName(stack: ItemStack): String = {
    val fl = getContainedFluid(stack)
    if (fl == null) {
      super.getItemStackDisplayName(stack)
    } else {
      super.getItemStackDisplayName(stack) + " - " + fl.getLocalizedName
    }
  }

  @SideOnly(Side.CLIENT)
  override def registerItemModels() = {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("pressure:canister.extended", "inventory"))
  }
}
