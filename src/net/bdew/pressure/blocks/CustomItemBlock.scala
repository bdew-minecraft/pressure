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
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World

class CustomItemBlock(bl: Block) extends ItemBlock(bl) {
  setRegistryName(bl.getRegistryName)
  setCreativeTab(PressureCreativeTabs.main)

  override def canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing, player: EntityPlayer, stack: ItemStack): Boolean =
    world.checkNoEntityCollision(bl.getCollisionBoundingBox(block.getDefaultState, world, pos)) && (
      Helper.tryPlaceBlock(world, pos, bl.getDefaultState, player, false) || Helper.tryPlaceBlock(world, pos.offset(side), bl.getDefaultState, player, false))

  override def onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    if (!world.isRemote && player.isInstanceOf[EntityPlayerMP]) {
      val p = player.asInstanceOf[EntityPlayerMP]
      if (Helper.tryPlaceBlock(world, pos, bl.getDefaultState, p, true) || Helper.tryPlaceBlock(world, pos.offset(side), bl.getDefaultState, p, true)) {
        if (!p.capabilities.isCreativeMode)
          player.inventory.decrStackSize(player.inventory.currentItem, 1)
        EnumActionResult.SUCCESS
      } else EnumActionResult.FAIL
    } else EnumActionResult.SUCCESS
  }
}
