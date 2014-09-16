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
import net.bdew.lib.data.{DataSlotInventory, DataSlotTank}
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.multiblock.interact.{CIFluidInput, CIFluidOutput, CIOutputFaces}
import net.bdew.lib.multiblock.tile.TileControllerGui
import net.bdew.pressure.blocks.tank.MachineTank
import net.bdew.pressure.config.Modules
import net.bdew.pressure.{Pressure, PressureResourceProvider}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.{Fluid, FluidContainerRegistry, FluidStack, IFluidContainerItem}

class TileTankController extends TileControllerGui with CIFluidInput with CIOutputFaces with CIFluidOutput {
  val cfg = MachineTank

  val resources = PressureResourceProvider

  val tank = new DataSlotTank("tank", this, 0)
  val inventory = new DataSlotInventory("inv", this, 3) {
    override def isItemValidForSlot(slot: Int, stack: ItemStack) =
      slot == 0 && stack != null && stack.getItem != null && (
        FluidContainerRegistry.isContainer(stack) || stack.getItem.isInstanceOf[IFluidContainerItem])
  }

  lazy val maxOutputs = 6

  def canEjectItem(stack: ItemStack) = {
    val outstack = inventory.getStackInSlot(1)
    (stack == null
      || stack.getItem == null
      || outstack == null
      || outstack.getItem == null
      || (ItemUtils.isSameItem(stack, outstack) && outstack.stackSize + stack.stackSize <= outstack.getMaxStackSize)
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
    val instack = inventory.getStackInSlot(0)
    if (instack != null && instack.getItem != null) {
      if (FluidContainerRegistry.isFilledContainer(instack)) {
        // This is a full container, add fluid into tank and return empty container (if defined)
        val fluid = FluidContainerRegistry.getFluidForFilledItem(instack)
        val cont = instack.getItem.getContainerItem(instack)
        if (fluid != null && inputFluid(fluid, false) == fluid.amount && canEjectItem(cont)) {
          inputFluid(fluid, true)
          inventory.decrStackSize(0, 1)
          doEjectItem(cont)
          inventory.markDirty()
        }
      } else if (FluidContainerRegistry.isEmptyContainer(instack)) {
        // This is an empty container, fill it
        for {
          filled <- Option(FluidContainerRegistry.fillFluidContainer(tank.getFluid, instack)) if canEjectItem(filled)
          filledFluid <- Option(FluidContainerRegistry.getFluidForFilledItem(filled))
        } {
          tank.drain(filledFluid.amount, true)
          inventory.decrStackSize(0, 1)
          doEjectItem(filled)
        }
      } else Misc.asInstanceOpt(instack.getItem, classOf[IFluidContainerItem]) map { incont =>
        // Its a fluid container, figure out what to do with it
        // Operating on a stack makes no sense, grab a single item
        val remstack = instack.copy()
        val opstack = remstack.splitStack(1)
        val influid = incont.getFluid(opstack)
        if (influid != null && influid.amount > 0) {
          // It has something, try to drain it
          val filled = incont.drain(opstack, tank.fill(influid.copy(), false), false)
          if (filled != null && filled.amount > 0) {
            // Can drain, grab a drained version and see if we can output it
            incont.drain(opstack, filled.amount, true)
            if (canEjectItem(opstack)) {
              // All good, proceed
              tank.fill(filled, true)
              doEjectItem(opstack)
              // put the remaining stack (if any) back
              if (remstack.stackSize > 0)
                inventory.setInventorySlotContents(0, remstack)
              else
                inventory.setInventorySlotContents(0, null)
            }
          }
        } else if (tank.getFluid != null) {
          // It's empty and we have fluid, try to fill it
          val filled = tank.drain(incont.fill(opstack, tank.getFluid.copy(), false), false)
          if (filled != null && filled.amount > 0) {
            // Can fill, grab a filled version and see if we can output it
            incont.fill(opstack, filled, true)
            if (canEjectItem(opstack)) {
              // All good, proceed
              tank.drain(filled.amount, true)
              doEjectItem(opstack)
              // put the remaining stack (if any) back
              if (remstack.stackSize > 0)
                inventory.setInventorySlotContents(0, remstack)
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
    tank.setCapacity(getNumOfMoudules("TankBlock") * Modules.TankBlock.capacity)
  }

  // === CIFluidInput ===

  def inputFluid(resource: FluidStack, doFill: Boolean): Int =
    if (canInputFluid(resource.getFluid)) tank.fill(resource, doFill) else 0

  def canInputFluid(fluid: Fluid) = tank.getFluid == null || tank.getFluid.getFluid == fluid
  def getTankInfo = Array(tank.getInfo)

  // === CIFluidOutput ===

  override def canOutputFluid(fluid: Fluid) = fluid == null || tank.getFluid.getFluid == fluid

  override def outputFluid(resource: FluidStack, doDrain: Boolean) =
    if (tank.getFluid == null || resource == null || resource.getFluid == null || tank.getFluid.getFluid != resource.getFluid)
      null
    else
      tank.drain(resource.amount, doDrain)

  override def outputFluid(ammount: Int, doDrain: Boolean) =
    if (tank.getFluid == null || ammount <= 0)
      null
    else
      tank.drain(ammount, doDrain)
}
