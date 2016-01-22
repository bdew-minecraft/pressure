/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import net.bdew.pressure.misc.PressureCreativeTabs
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.block.Block
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

class CustomItemBlock(bl: Block) extends ItemBlock(bl) {
  override def func_150936_a(w: World, x: Int, y: Int, z: Int, side: Int, p: EntityPlayer, s: ItemStack) = {
    true
  }

  setCreativeTab(PressureCreativeTabs.main)

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, xOff: Float, yOff: Float, zOff: Float): Boolean = {
    if (!world.isRemote && player.isInstanceOf[EntityPlayerMP]) {
      val p = player.asInstanceOf[EntityPlayerMP]
      val dir = ForgeDirection.values()(side)
      if (Helper.tryPlaceBlock(world, x, y, z, bl, p) || Helper.tryPlaceBlock(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, bl, p)) {
        if (!p.capabilities.isCreativeMode)
          player.inventory.decrStackSize(player.inventory.currentItem, 1)
        true
      } else false
    } else true
  }
}
