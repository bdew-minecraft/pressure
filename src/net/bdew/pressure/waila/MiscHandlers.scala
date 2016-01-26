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
import net.bdew.lib.Misc
import net.bdew.pressure.blocks.TileFilterable
import net.bdew.pressure.blocks.input.TileInput
import net.bdew.pressure.blocks.output.TileOutput
import net.bdew.pressure.blocks.pump.TilePump
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

object WailaPumpHandler extends BaseDataProvider(classOf[TilePump]) {
  override def getBodyStrings(target: TilePump, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    if (target.getFacing == acc.getSide)
      List(Misc.toLocal("pressure.waila.side.output"))
    else if (target.getFacing.getOpposite == acc.getSide)
      List(Misc.toLocal("pressure.waila.side.input"))
    else
      List(Misc.toLocal("pressure.waila.side.nothing"))
  }
}

object WailaPressureInputHandler extends BaseDataProvider(classOf[TileInput]) {
  override def getBodyStrings(target: TileInput, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    if (target.getFacing == acc.getSide)
      List(Misc.toLocal("pressure.waila.side.output"), Misc.toLocal("pressure.waila.side.pressure"))
    else if (target.getFacing.getOpposite == acc.getSide)
      List(Misc.toLocal("pressure.waila.side.input"), Misc.toLocal("pressure.waila.side.fluid"))
    else
      List(Misc.toLocal("pressure.waila.side.nothing"))
  }
}

object WailaPressureOutputHandler extends BaseDataProvider(classOf[TileOutput]) {
  override def getBodyStrings(target: TileOutput, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    if (target.getFacing == acc.getSide)
      List(Misc.toLocal("pressure.waila.side.output"), Misc.toLocal("pressure.waila.side.fluid"))
    else if (target.getFacing.getOpposite == acc.getSide)
      List(Misc.toLocal("pressure.waila.side.input"), Misc.toLocal("pressure.waila.side.pressure"))
    else
      List(Misc.toLocal("pressure.waila.side.nothing"))
  }
}

object WailaFilterableHandler extends BaseDataProvider(classOf[TileFilterable]) {
  override def getBodyStrings(target: TileFilterable, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = {
    target.getFluidFilter map { fluid =>
      Misc.toLocalF("pressure.waila.filter", fluid.getLocalizedName(new FluidStack(fluid, 1)))
    }
  }
}