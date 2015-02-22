/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.lib.{DecFormat, Misc}
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

object WailaTankProvider extends BaseDataProvider(classOf[TileTankController]) {
  override def getBodyStrings(target: TileTankController, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    var out = List.empty[String]
    val fluid = target.tank.getFluid
    if (fluid != null && fluid.getFluid != null && fluid.amount > 0) {
      out :+= fluid.getFluid.getLocalizedName(fluid)
      out :+= "%s/%s mB".format(DecFormat.round(fluid.amount), DecFormat.round(target.tank.getCapacity))
    } else {
      out :+= Misc.toLocal("bdlib.label.empty")
    }
    target.getFluidFilter map { filter =>
      out :+= Misc.toLocalF("pressure.waila.filter", filter.getLocalizedName(new FluidStack(filter, 1)))
    }
    out
  }
}
