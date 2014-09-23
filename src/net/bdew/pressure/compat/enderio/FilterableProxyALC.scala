/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.enderio

import net.bdew.pressure.api.IFilterable
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.{Fluid, FluidStack}

class FilterableProxyALC(val b: TileEntity) extends IFilterable {

  import net.bdew.pressure.compat.enderio.EnderIOReflect._

  override def setFluidFilter(fluid: Fluid) {
    for {
      cond <- Option(mTCBgetConduit.invoke(b, cALC))
      net <- Option(mALCgetNetwork.invoke(cond))
    } {
      mALCNsetFluidType.invoke(net, new FluidStack(fluid, 1))
      mALCNsetFluidTypeLocked.invoke(net, Boolean.box(true))
    }
  }

  override def clearFluidFilter() {
    for {
      cond <- Option(mTCBgetConduit.invoke(b, cALC))
      net <- Option(mALCgetNetwork.invoke(cond))
    } {
      mALCNsetFluidTypeLocked.invoke(net, Boolean.box(false))
    }
  }
}

