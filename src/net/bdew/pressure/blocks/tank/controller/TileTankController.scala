/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.controller

import net.bdew.lib.Misc
import net.bdew.lib.data.base.UpdateKind
import net.bdew.lib.data.{DataSlotInventory, DataSlotString, DataSlotTank}
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.multiblock.interact.{CIFluidInput, CIFluidOutput, CIOutputFaces}
import net.bdew.lib.multiblock.tile.TileControllerGui
import net.bdew.lib.sensors.SensorSystem
import net.bdew.lib.sensors.multiblock.CIRedstoneSensors
import net.bdew.pressure.blocks.tank.blocks.TileTankIndicator
import net.bdew.pressure.blocks.tank.{CIFilterable, MachineTank, ModuleNeedsRenderUpdate}
import net.bdew.pressure.config.Modules
import net.bdew.pressure.sensor.Sensors
import net.bdew.pressure.{Pressure, PressureResourceProvider}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids._

class TileTankController extends TileControllerGui with CIFluidInput with CIOutputFaces with CIFluidOutput with CIFilterable with CIRedstoneSensors {
  val cfg = MachineTank

  val resources = PressureResourceProvider

  val fluidFilter = DataSlotString("fluidFilter", this).setUpdate(UpdateKind.GUI, UpdateKind.SAVE, UpdateKind.WORLD)

  val tank = new DataSlotTank("tank", this, 0) {
    setUpdate(UpdateKind.SAVE, UpdateKind.WORLD, UpdateKind.GUI)
    override val sendCapacityOnUpdateKind = Set(UpdateKind.WORLD, UpdateKind.GUI)
  }

  var lastRenderUpdate = 0L
  var needsRenderUpdate = false

  val inventory = new DataSlotInventory("inv", this, 3) {
    override def isItemValidForSlot(slot: Int, stack: ItemStack) =
      slot == 0 && stack != null && stack.getItem != null && (
        FluidContainerRegistry.isContainer(stack) || stack.getItem.isInstanceOf[IFluidContainerItem])
  }

  def doRenderUpdate(): Unit = {
    needsRenderUpdate = false
    lastRenderUpdate = worldObj.getTotalWorldTime
    for (ref <- modules if ref.block(worldObj).exists(_.isInstanceOf[ModuleNeedsRenderUpdate]))
      worldObj.markBlockRangeForRenderUpdate(ref.x, ref.y, ref.z, ref.x, ref.y, ref.z)
  }

  handleClientUpdate listen { tag =>
    if (worldObj.getTotalWorldTime > lastRenderUpdate + 10)
      doRenderUpdate()
    else
      needsRenderUpdate = true
  }

  clientTick.listen(() => {
    if (needsRenderUpdate && worldObj.getTotalWorldTime > lastRenderUpdate + 10)
      doRenderUpdate()
  })

  lazy val maxOutputs = 6

  def getFluidFilter = Option(fluidFilter.value) flatMap (x => Option(FluidRegistry.getFluid(x)))

  // CIFilterable

  override def clearFluidFilter() = fluidFilter := null
  override def setFluidFilter(fluid: Fluid) = fluidFilter := (if (fluid == null) null else fluid.getName)

  // === Inventory Stuff ===

  def canEjectItem(stack: ItemStack) = {
    val outStack = inventory.getStackInSlot(1)
    (stack == null
      || stack.getItem == null
      || outStack == null
      || outStack.getItem == null
      || (ItemUtils.isSameItem(stack, outStack) && outStack.stackSize + stack.stackSize <= outStack.getMaxStackSize)
      )
  }

  def doEjectItem(stack: ItemStack) {
    if (stack != null && stack.getItem != null) {
      stack.stackSize = 1
      if (inventory.getStackInSlot(1) == null) {
        inventory.setInventorySlotContents(1, stack)
      } else {
        inventory.getStackInSlot(1).stackSize += stack.stackSize
        inventory.markDirty()
      }
    }
  }

  def doUpdate() {
    val inStack = inventory.getStackInSlot(0)
    if (inStack != null && inStack.getItem != null) {
      if (FluidContainerRegistry.isFilledContainer(inStack)) {
        // This is a full container, add fluid into tank and return empty container (if defined)
        val fluid = FluidContainerRegistry.getFluidForFilledItem(inStack)
        val cont = inStack.getItem.getContainerItem(inStack)
        if (fluid != null && inputFluid(fluid, false) == fluid.amount && canEjectItem(cont)) {
          inputFluid(fluid, true)
          inventory.decrStackSize(0, 1)
          doEjectItem(cont)
          inventory.markDirty()
        }
      } else if (FluidContainerRegistry.isEmptyContainer(inStack)) {
        // This is an empty container, fill it
        for {
          filled <- Option(FluidContainerRegistry.fillFluidContainer(tank.getFluid, inStack)) if canEjectItem(filled)
          filledFluid <- Option(FluidContainerRegistry.getFluidForFilledItem(filled))
        } {
          outputFluid(filledFluid.amount, true)
          inventory.decrStackSize(0, 1)
          doEjectItem(filled)
        }
      } else Misc.asInstanceOpt(inStack.getItem, classOf[IFluidContainerItem]) foreach { inCont =>
        // Its a fluid container, figure out what to do with it
        // Operating on a stack makes no sense, grab a single item
        val remStack = inStack.copy()
        val opStack = remStack.splitStack(1)
        val inFluid = inCont.getFluid(opStack)
        if (inFluid != null && inFluid.amount > 0) {
          // It has something, try to drain it
          val filled = inCont.drain(opStack, inputFluid(inFluid.copy(), false), false)
          if (filled != null && filled.amount > 0) {
            // Can drain, grab a drained version and see if we can output it
            inCont.drain(opStack, filled.amount, true)
            if (canEjectItem(opStack)) {
              // All good, proceed
              inputFluid(filled, true)
              doEjectItem(opStack)
              // put the remaining stack (if any) back
              if (remStack.stackSize > 0)
                inventory.setInventorySlotContents(0, remStack)
              else
                inventory.setInventorySlotContents(0, null)
            }
          }
        } else if (tank.getFluid != null) {
          // It's empty and we have fluid, try to fill it
          val filled = outputFluid(inCont.fill(opStack, tank.getFluid.copy(), false), false)
          if (filled != null && filled.amount > 0) {
            // Can fill, grab a filled version and see if we can output it
            inCont.fill(opStack, filled, true)
            if (canEjectItem(opStack)) {
              // All good, proceed
              outputFluid(filled.amount, true)
              doEjectItem(opStack)
              // put the remaining stack (if any) back
              if (remStack.stackSize > 0)
                inventory.setInventorySlotContents(0, remStack)
              else
                inventory.setInventorySlotContents(0, null)
            }
          }
        }
      }
    }
  }

  serverTick.listen(doUpdate)

  override def openGui(player: EntityPlayer) = player.openGui(Pressure, cfg.guiId, worldObj, xCoord, yCoord, zCoord)

  def onModulesChanged() {
    val newCapacity = 1.0 * getNumOfModules("TankBlock") * Modules.TankBlock.capacity

    if (newCapacity <= Int.MaxValue)
      tank.setCapacity(newCapacity.toInt)
    else
      tank.setCapacity(Int.MaxValue)

    // If we don't have indicators - don't spam updates
    if (modules.exists(_.getTile[TileTankIndicator](worldObj).isDefined)) {
      tank.setUpdate(UpdateKind.SAVE, UpdateKind.GUI, UpdateKind.WORLD)
    } else {
      tank.setUpdate(UpdateKind.SAVE, UpdateKind.GUI)
    }

    if (newCapacity == 0)
      tank.setFluid(null)
    else if (tank.getFluid != null && tank.getFluid.amount > newCapacity)
      tank.getFluid.amount = newCapacity.toInt

    dataSlotChanged(tank) // ensure update sent to client
  }

  def dropItems() {
    if (getWorldObj != null && !getWorldObj.isRemote) {
      for (stack <- inventory.inv if stack != null) {
        ItemUtils.throwItemAt(getWorldObj, xCoord, yCoord, zCoord, stack)
      }
      inventory.inv = new Array[ItemStack](inventory.size)
    }
  }

  def isReady = !revalidateOnNextTick && !modulesChanged

  // === CIFluidInput ===

  def inputFluid(resource: FluidStack, doFill: Boolean): Int =
    if (canInputFluid(resource.getFluid)) tank.fill(resource, doFill) else 0

  def canInputFluid(fluid: Fluid) =
    isReady && (tank.getFluid == null || tank.getFluid.getFluid == fluid) &&
      ((fluidFilter :== null) || (fluidFilter :== fluid.getName))

  def getTankInfo = Array(tank.getInfo)

  // === CIFluidOutput ===

  override def canOutputFluid(fluid: Fluid) = fluid == null || (tank.getFluid != null && tank.getFluid.getFluid == fluid)

  override def outputFluid(resource: FluidStack, doDrain: Boolean) =
    if (!isReady || tank.getFluid == null || resource == null || resource.getFluid == null || tank.getFluid.getFluid != resource.getFluid)
      null
    else
      tank.drain(resource.amount, doDrain)

  override def outputFluid(amount: Int, doDrain: Boolean) =
    if (!isReady || tank.getFluid == null || amount <= 0)
      null
    else
      tank.drain(amount, doDrain)

  override def redstoneSensorSystem: SensorSystem[TileEntity, Boolean] = Sensors
  override def redstoneSensorsType = Sensors.tankSensors
}
