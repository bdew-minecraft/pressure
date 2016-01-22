/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.enderio

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.FluidStack

object EnderIOReflect {
  type TileConduitBundle = TileEntity {
    def getConduit[T](cls: Class[T]): T
  }

  type LiquidConduit = {
    def getNetwork: LiquidConduitNetwork
  }

  type LiquidConduitNetwork = {
    def setFluidType(f: FluidStack): Unit
    def setFluidTypeLocked(b: Boolean): Unit
  }

  val clsTileConduitBundle = Class.forName("crazypants.enderio.conduit.TileConduitBundle").asInstanceOf[Class[TileConduitBundle]]
  val clsAdvancedLiquidConduit = Class.forName("crazypants.enderio.conduit.liquid.AdvancedLiquidConduit").asInstanceOf[Class[LiquidConduit]]
  val clsLiquidConduit = Class.forName("crazypants.enderio.conduit.liquid.LiquidConduit").asInstanceOf[Class[LiquidConduit]]
}
