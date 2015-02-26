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
import net.bdew.lib.data.base.UpdateKind
import net.bdew.lib.{DecFormat, Misc}
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack

object WailaTankProvider extends BaseDataProvider(classOf[TileTankController]) {
  override def getNBTTag(player: EntityPlayerMP, te: TileTankController, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int) = {
    tag.setTag("pressure_waila_tank_data", Misc.applyMutator(new NBTTagCompound) {
      te.doSave(UpdateKind.GUI, _)
    })
    tag
  }

  override def getBodyStrings(target: TileTankController, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    if (acc.getNBTData.hasKey("pressure_waila_tank_data")) {
      target.doLoad(UpdateKind.GUI, acc.getNBTData.getCompoundTag("pressure_waila_tank_data"))
    }
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
