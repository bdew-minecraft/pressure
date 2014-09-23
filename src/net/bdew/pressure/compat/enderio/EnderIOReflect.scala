/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.enderio

import net.minecraftforge.fluids.FluidStack

object EnderIOReflect {
  val cTCB = Class.forName("crazypants.enderio.conduit.TileConduitBundle")
  val mTCBgetConduit = cTCB.getMethod("getConduit", classOf[Class[_]])

  val cALC = Class.forName("crazypants.enderio.conduit.liquid.AdvancedLiquidConduit")
  val mALCgetNetwork = cALC.getMethod("getNetwork")

  val cALCN = Class.forName("crazypants.enderio.conduit.liquid.AdvancedLiquidConduitNetwork")
  val mALCNsetFluidType = cALCN.getMethod("setFluidType", classOf[FluidStack])
  val mALCNsetFluidTypeLocked = cALCN.getMethod("setFluidTypeLocked", classOf[Boolean])

  val cLC = Class.forName("crazypants.enderio.conduit.liquid.LiquidConduit")
  val mLCgetNetwork = cLC.getMethod("getNetwork")

  val cLCN = Class.forName("crazypants.enderio.conduit.liquid.LiquidConduitNetwork")
  val mLCNsetFluidType = cLCN.getMethod("setFluidType", classOf[FluidStack])
  val mLCNsetFluidTypeLocked = cLCN.getMethod("setFluidTypeLocked", classOf[Boolean])

}
