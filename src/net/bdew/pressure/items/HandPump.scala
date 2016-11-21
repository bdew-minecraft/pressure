/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items

import net.bdew.lib.capabilities.helpers.FluidHelper
import net.bdew.lib.items.BaseItem
import net.bdew.pressure.config.Tuning
import net.bdew.pressure.misc.UnstackingFluidHandler
import net.minecraft.block.material.Material
import net.minecraft.block.{Block, BlockLiquid}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{ActionResult, EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fluids._
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object HandPump extends BaseItem("HandPump") {
  lazy val cfg = Tuning.getSection("Items").getSection(name)
  lazy val maxDrain = cfg.getInt("MaxDrain")

  setMaxStackSize(1)
  MinecraftForge.EVENT_BUS.register(this)

  def findFillTarget(fs: FluidStack, player: EntityPlayer, mustTakeAll: Boolean): IFluidHandler = {
    if (fs == null) return null
    for {
      i <- 0 until player.inventory.getSizeInventory
      stack <- Option(player.inventory.getStackInSlot(i)) if stack.getItem != null
      handler <- UnstackingFluidHandler.getIfNeeded(player, i)
    } {
      val canFill = handler.fill(fs, false)
      if ((mustTakeAll && canFill == fs.amount) || (!mustTakeAll && canFill > 0)) return handler
    }
    return null
  }

  def drainBlock(world: World, block: Block, pos: BlockPos, stack: ItemStack, dir: EnumFacing, player: EntityPlayer): Boolean = {
    if (block.isInstanceOf[IFluidBlock]) {
      val bl = block.asInstanceOf[IFluidBlock]
      val fl = bl.drain(world, pos, false)
      val toFill = findFillTarget(fl, player, true)
      if (toFill != null) {
        if (!world.isRemote) {
          toFill.fill(bl.drain(world, pos, true), true)
        }
        return true
      }
    } else {
      val bState = world.getBlockState(pos)
      if (bState.getBlock.isInstanceOf[BlockLiquid] && bState.getBlock.getMaterial(bState) == Material.WATER && bState.getValue(BlockLiquid.LEVEL) == 0) {
        val ns = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME)
        val toFill = findFillTarget(ns, player, true)
        if (toFill != null) {
          if (!world.isRemote) {
            world.setBlockToAir(pos)
            toFill.fill(ns, true)
          }
          return true
        }
      } else if (bState.getBlock.isInstanceOf[BlockLiquid] && bState.getBlock.getMaterial(bState) == Material.LAVA && bState.getValue(BlockLiquid.LEVEL) == 0) {
        val ns = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME)
        val toFill = findFillTarget(ns, player, true)
        if (toFill != null) {
          if (!world.isRemote) {
            world.setBlockToAir(pos)
            toFill.fill(ns, true)
          }
          return true
        }
      } else {
        FluidHelper.getFluidHandler(world, pos, dir) foreach { handler =>
          if (!world.isRemote) {
            val fs = handler.drain(maxDrain, false)
            val toFill = findFillTarget(fs, player, false)
            if (toFill != null) {
              val canFill = toFill.fill(fs, false)
              if (canFill > 0) {
                toFill.fill(handler.drain(canFill, true), true)
                return true
              }
            }
          } else return true
        }
      }
    }
    return false
  }

  override def onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
    val stack = player.getHeldItem(hand)
    if (player.isSneaking) return new ActionResult[ItemStack](EnumActionResult.PASS, stack)
    val mop = rayTrace(world, player, true)
    if (mop == null) return new ActionResult[ItemStack](EnumActionResult.PASS, stack)

    val block = world.getBlockState(mop.getBlockPos).getBlock

    if (drainBlock(world, block, mop.getBlockPos, stack, mop.sideHit.getOpposite, player)) {
      if (!world.isRemote)
        player.inventoryContainer.detectAndSendChanges()
      player.swingArm(hand)
      player.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5F, world.rand.nextFloat * 0.1F + 0.9F)
      return new ActionResult[ItemStack](EnumActionResult.SUCCESS, stack)
    }

    return new ActionResult[ItemStack](EnumActionResult.FAIL, stack)
  }

  @SubscribeEvent
  def onInteract(ev: PlayerInteractEvent.RightClickBlock) {
    val item = ev.getEntityPlayer.getHeldItem(EnumHand.MAIN_HAND)
    if (item != null && item.getItem == this && !ev.getEntityPlayer.isSneaking) ev.setCanceled(true)
  }
}
