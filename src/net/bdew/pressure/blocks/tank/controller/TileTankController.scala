/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.controller

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.capabilities.helpers.FluidHelper
import net.bdew.lib.data.base.{DataSlot, UpdateKind}
import net.bdew.lib.data.{DataSlotInventory, DataSlotOption, DataSlotTank}
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.multiblock.interact.{CIFluidInput, CIFluidOutput, CIOutputFaces}
import net.bdew.lib.multiblock.tile.TileControllerGui
import net.bdew.lib.sensors.SensorSystem
import net.bdew.lib.sensors.multiblock.CIRedstoneSensors
import net.bdew.pressure.api.properties.IFilterable
import net.bdew.pressure.blocks.tank.blocks.{BlockFluidAccess, BlockTankIndicator}
import net.bdew.pressure.blocks.tank.{CIFilterable, MachineTank, ModuleNeedsRenderUpdate}
import net.bdew.pressure.config.Modules
import net.bdew.pressure.misc.CountedDataSlotTank
import net.bdew.pressure.sensor.Sensors
import net.bdew.pressure.{Pressure, PressureResourceProvider}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids._
import net.minecraftforge.fluids.capability.IFluidHandler

class TileTankController extends TileControllerGui with CIFluidInput with CIOutputFaces with CIFluidOutput with CIFilterable with CIRedstoneSensors {
  val cfg = MachineTank

  val resources = PressureResourceProvider

  val fluidFilter = DataSlotOption[Fluid]("fluidFilter", this).setUpdate(UpdateKind.GUI, UpdateKind.SAVE, UpdateKind.WORLD)

  val tank = new DataSlotTank("tank", this, 0) with CountedDataSlotTank {
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
    for (ref <- getModuleBlocks[ModuleNeedsRenderUpdate].keys)
      worldObj.markBlockRangeForRenderUpdate(ref, ref)
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

  // CIFilterable

  def getFluidFilter = fluidFilter.value

  override val filterableCapability = new IFilterable {
    override def clearFluidFilter() = fluidFilter.unset()
    override def setFluidFilter(fluid: Fluid) = if (fluid == null) fluidFilter.unset() else fluidFilter.set(fluid)
  }

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
    val original = inventory.getStackInSlot(0)
    if (original != null) {
      val inStack = original.copy()
      inStack.stackSize = 1
      for (handler <- FluidHelper.getFluidHandler(inStack)) {
        if (tank.getFluidAmount > 0 && handler.getTankProperties.exists(t => t.canFill && (t.getContents == null || t.getContents.amount < t.getCapacity))) {
          // Attempt to fill
          val drained = tank.drain(Int.MaxValue, false)
          drained.amount = handler.fill(drained.copy(), true)
          if (drained.amount > 0 && canEjectItem(inStack)) {
            tank.drain(drained, true)
            doEjectItem(inStack)
            inventory.decrStackSize(0, 1)
          }
        } else if (tank.getFluidAmount < tank.getCapacity && handler.getTankProperties.exists(t => t.canDrain && t.getContents != null && t.getContents.amount > 0)) {
          // Attempt to drain
          val drained = handler.drain(Int.MaxValue, false)
          drained.amount = tank.fill(drained.copy(), false)
          if (drained.amount > 0) {
            handler.drain(drained, true)
            if (canEjectItem(inStack)) {
              tank.fill(drained, true)
              doEjectItem(inStack)
              inventory.decrStackSize(0, 1)
            }
          }
        }
      }
    }
  }

  serverTick.listen(doUpdate)

  override def openGui(player: EntityPlayer) = player.openGui(Pressure, cfg.guiId, worldObj, pos.getX, pos.getY, pos.getZ)

  def onModulesChanged() {
    val newCapacity = 1.0 * getNumOfModules("TankBlock") * Modules.TankBlock.capacity

    if (newCapacity <= Int.MaxValue)
      tank.setCapacity(newCapacity.toInt)
    else
      tank.setCapacity(Int.MaxValue)

    // If we don't have indicators - don't spam updates
    if (getModulePositions(BlockTankIndicator).nonEmpty) {
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
    if (getWorld != null && !getWorld.isRemote) {
      for (stack <- inventory.inv if stack != null) {
        ItemUtils.throwItemAt(getWorld, pos, stack)
      }
      inventory.inv = new Array[ItemStack](inventory.size)
    }
  }

  def isReady = !revalidateOnNextTick && !modulesChanged

  override def dataSlotChanged(slot: DataSlot): Unit = {
    super.dataSlotChanged(slot)
    if (slot == tank) {
      // Send block updates if tank content changes - needed for extracells, etc.
      for (pos <- getModulePositions(BlockFluidAccess)) {
        worldObj.notifyNeighborsOfStateChange(pos, BlockFluidAccess)
      }
    }
  }

  override def getInputTanks: List[IFluidHandler] =
    if (isReady) List(tank) else List.empty

  override def getOutputTanks: List[IFluidHandler] =
    if (isReady) List(tank) else List.empty

  override def redstoneSensorSystem: SensorSystem[TileEntity, Boolean] = Sensors
  override def redstoneSensorsType = Sensors.tankSensors
}
