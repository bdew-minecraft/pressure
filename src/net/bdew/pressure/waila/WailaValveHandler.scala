/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.lib.{DecFormat, Misc}
import net.bdew.pressure.blocks.valves.BlockValve
import net.bdew.pressure.blocks.valves.check.BlockCheckValve
import net.bdew.pressure.blocks.valves.sensor.TilePipeSensor
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumChatFormatting
import net.minecraft.world.World
import net.minecraftforge.fluids.{Fluid, FluidRegistry, FluidStack}

object WailaValveHandler extends BaseDataProvider(classOf[BlockValve]) {
  override def getBodyStrings(target: BlockValve, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    var list = List.empty[String]

    if ((acc.getMetadata & 7) == acc.getSide.ordinal())
      list :+= Misc.toLocal("pressure.waila.side.output")
    else if ((acc.getMetadata & 7) == acc.getSide.getOpposite.ordinal())
      list :+= Misc.toLocal("pressure.waila.side.input")

    if (target == BlockCheckValve)
      if ((acc.getMetadata & 8) == 0)
        list :+= Misc.toLocal("pressure.waila.valve.open")
      else
        list :+= Misc.toLocal("pressure.waila.valve.closed")

    list
  }
}

object WailaPipeSensorHandler extends BaseDataProvider(classOf[TilePipeSensor]) {
  override def getNBTTag(player: EntityPlayerMP, te: TilePipeSensor, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int) = {
    val data = new NBTTagCompound
    for ((fluid, amount) <- te.averages.getAverages.toList.filter(_._2 > 0.000001)) {
      data.setDouble(fluid.getName, amount)
    }
    tag.setTag("flow_averages", data)
    tag.setInteger("flow_averages_size", te.averages.values.size)
    tag
  }

  def makeFluidList(averages: List[(Fluid, Double)]) = {
    for ((fluid, amount) <- averages) yield " * %s%s%s - %s%s%s mB/t".format(
      EnumChatFormatting.YELLOW,
      fluid.getLocalizedName(new FluidStack(fluid, amount.toInt)),
      EnumChatFormatting.RESET,
      EnumChatFormatting.YELLOW,
      DecFormat.short(amount),
      EnumChatFormatting.RESET
    )
  }

  override def getBodyStrings(target: TilePipeSensor, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = {
    import scala.collection.JavaConversions._
    if ((acc.getMetadata & 8) == 8) {
      if (acc.getNBTData.hasKey("flow_averages")) {
        val data = acc.getNBTData.getCompoundTag("flow_averages")
        val averages = (for {
          name <- Misc.filterType(data.func_150296_c, classOf[String]) if FluidRegistry.isFluidRegistered(name)
          fluid <- Option(FluidRegistry.getFluid(name))
        } yield fluid -> data.getDouble(name)).toList.sortBy(-_._2)
        List(
          EnumChatFormatting.UNDERLINE +
            Misc.toLocalF("pressure.message.flow.head", acc.getNBTData.getInteger("flow_averages_size"))
            + EnumChatFormatting.RESET
        ) ++ makeFluidList(averages)

      } else {
        List(Misc.toLocal("pressure.message.flow.unknown"))
      }
    } else {
      List(Misc.toLocal("pressure.message.flow.empty"))
    }
  }
}