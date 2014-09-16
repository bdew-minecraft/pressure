/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.controller

import net.bdew.lib.data.{DataSlotInventory, DataSlotTank}
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

  def doUpdate() {
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
