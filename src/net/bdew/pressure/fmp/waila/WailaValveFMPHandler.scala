/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaFMPAccessor}
import net.bdew.lib.Misc
import net.bdew.pressure.misc.DataSlotFluidAverages
import net.bdew.pressure.waila.WailaPipeSensorHandler
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumChatFormatting

object WailaValveFMPHandler extends BaseFMPDataProvider {
  override def getBodyStrings(stack: ItemStack, acc: IWailaFMPAccessor, cfg: IWailaConfigHandler) = {
    var list = List.empty[String]

    val direction = Misc.forgeDirection(acc.getNBTData.getByte("facing"))
    if (direction.ordinal == acc.getPosition.sideHit)
      list :+= Misc.toLocal("pressure.waila.side.output")
    else if (direction.getOpposite.ordinal == acc.getPosition.sideHit)
      list :+= Misc.toLocal("pressure.waila.side.input")

    if (acc.getID == "bdew.pressure.checkvalve") {
      if (acc.getNBTData.getBoolean("state"))
        list :+= Misc.toLocal("pressure.waila.valve.open")
      else
        list :+= Misc.toLocal("pressure.waila.valve.closed")
    } else if (acc.getID == "bdew.pressure.pipesensor") {
      val bytes = acc.getNBTData.getByteArray("flow")
      if (acc.getNBTData.getBoolean("state") && bytes.nonEmpty) {
        val data = DataSlotFluidAverages.unSerializeAverages(bytes)
        val values = DataSlotFluidAverages.getAverages(data).filter(_._2 > 0.000001).toList.sortBy(-_._2)
        if (values.nonEmpty) {
          list :+= EnumChatFormatting.UNDERLINE + Misc.toLocalF("pressure.message.flow.head", data.size) + EnumChatFormatting.RESET
          list ++= WailaPipeSensorHandler.makeFluidList(values)
        } else {
          list :+= Misc.toLocal("pressure.message.flow.empty")
        }
      } else {
        list :+= Misc.toLocal("pressure.message.flow.empty")
      }
    }

    list
  }
}
