/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.blocks

import net.bdew.pressure.misc.{PressureCreativeTabs, Helper}
import net.minecraft.block.Block
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

class ItemBlockPipe(bl: Block) extends ItemBlock(bl) {
  override def func_150936_a(w: World, x: Int, y: Int, z: Int, side: Int, p: EntityPlayer, s: ItemStack) = {
    true
  }

  setCreativeTab(PressureCreativeTabs.main)

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, xOff: Float, yOff: Float, zOff: Float): Boolean = {
    if (player.isSneaking) return false
    if (!world.isRemote && player.isInstanceOf[EntityPlayerMP]) {
      val p = player.asInstanceOf[EntityPlayerMP]
      val dir = ForgeDirection.values()(side)
      if (Helper.tryPlacePipe(world, x, y, z, p) || Helper.tryPlacePipe(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, p)) {
        if (!p.capabilities.isCreativeMode)
          player.inventory.decrStackSize(player.inventory.currentItem, 1)
        true
      } else false
    } else true
  }
}
