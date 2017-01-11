/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank

import net.bdew.lib.Client
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.Machine
import net.bdew.lib.multiblock.MachineCore
import net.bdew.pressure.blocks.tank.controller.{BlockTankController, TileTankController}
import net.bdew.pressure.blocks.tank.gui.{ContainerTank, GuiTank}
import net.bdew.pressure.network.{MsgTankUpdate, NetworkHandler}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.{FluidRegistry, FluidStack}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object MachineTank extends Machine("TankController", BlockTankController) with MachineCore with GuiProvider {
  def guiId: Int = 1
  type TEClass = TileTankController

  @SideOnly(Side.CLIENT)
  def getGui(te: TileTankController, player: EntityPlayer) = new GuiTank(te, player)
  def getContainer(te: TileTankController, player: EntityPlayer) = new ContainerTank(te, player)

  NetworkHandler.regClientHandler {
    case MsgTankUpdate(x, y, z, fluid, amount, capacity) =>
      Client.world.getTileEntity(new BlockPos(x, y, z)) match {
        case controller: TileTankController =>
          controller.tank.setCapacity(capacity)
          if (fluid == "") {
            controller.tank.setFluid(null)
          } else {
            controller.tank.setFluid(new FluidStack(FluidRegistry.getFluid(fluid), amount))
          }
        case _ => // we don't have that controller loaded, pass
      }
  }
}
