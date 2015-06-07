/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items.configurator

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.items.SimpleItem
import net.bdew.lib.player.PlayerCache
import net.bdew.pressure.Pressure
import net.bdew.pressure.api.IFilterable
import net.bdew.pressure.config.Config
import net.bdew.pressure.network.NetworkHandler
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.fluids.{FluidRegistry, FluidStack}

object ItemConfigurator extends SimpleItem("Configurator") with GuiProvider {
  override def guiId = 2
  override type TEClass = Any

  Config.guiHandler.register(this)

  val filterableCache = new PlayerCache[IFilterable]

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiConfigurator(player)

  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerConfigurator(player)

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, xOff: Float, yOff: Float, zOff: Float): Boolean = {
    if (!world.isRemote && player.isInstanceOf[EntityPlayerMP]) {
      Option(Helper.getFilterableForWorldCoordinates(world, x, y, z, side)) foreach { filterable =>
        filterableCache.update(player, filterable)
        player.openGui(Pressure, ItemConfigurator.guiId, world, x, y, z)
      }
    }
    true
  }

  NetworkHandler.regServerHandler {
    case (msg: MsgSetFluidFilter, p: EntityPlayerMP) =>
      for {
        cont <- Misc.asInstanceOpt(p.openContainer, classOf[ContainerConfigurator])
        fluid <- Option(FluidRegistry.getFluid(msg.fluid))
        filterable <- filterableCache.map.get(p)
      } {
        p.addChatMessage(new ChatComponentTranslation("pressure.label.filter.set", fluid.getLocalizedName(new FluidStack(fluid, 1))))
        filterable.setFluidFilter(fluid)
        filterableCache.reset(p)
        p.closeContainer()
      }
    case (msg: MsgUnsetFluidFilter, p: EntityPlayerMP) =>
      for {
        cont <- Misc.asInstanceOpt(p.openContainer, classOf[ContainerConfigurator])
        filterable <- filterableCache.map.get(p)
      } {
        p.addChatMessage(new ChatComponentTranslation("pressure.label.filter.unset"))
        filterable.clearFluidFilter()
        filterableCache.reset(p)
        p.closeContainer()
      }
  }

}
