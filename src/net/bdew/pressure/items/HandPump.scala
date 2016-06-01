/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items

import net.bdew.lib.items.BaseItem
import net.bdew.pressure.config.Tuning
import net.minecraft.block.material.Material
import net.minecraft.block.{Block, BlockLiquid}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{ActionResult, EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fluids._
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object HandPump extends BaseItem("HandPump") {
  lazy val cfg = Tuning.getSection("Items").getSection(name)
  lazy val maxDrain = cfg.getInt("MaxDrain")

  setMaxStackSize(1)
  MinecraftForge.EVENT_BUS.register(this)

  def findFillTarget(fs: FluidStack, inventory: IInventory, mustTakeAll: Boolean): ItemStack = {
    if (fs == null) return null
    for (i <- 0 until inventory.getSizeInventory) {
      val item = inventory.getStackInSlot(i)
      if (item != null && item.getItem != null && item.stackSize == 1 && item.getItem.isInstanceOf[IFluidContainerItem]) {
        val fc = item.getItem.asInstanceOf[IFluidContainerItem]
        val canFill = fc.fill(item, fs, false)
        if ((mustTakeAll && canFill == fs.amount) || (!mustTakeAll && canFill > 0)) return item
      }
    }
    return null
  }

  def drainBlock(world: World, block: Block, pos: BlockPos, stack: ItemStack, dir: EnumFacing, player: EntityPlayer): Boolean = {
    if (block.isInstanceOf[BlockFluidBase]) {
      val bl = block.asInstanceOf[BlockFluidBase]
      val fl = bl.drain(world, pos, false)
      val toFill = findFillTarget(fl, player.inventory, true)
      if (toFill != null) {
        if (!world.isRemote) {
          toFill.getItem.asInstanceOf[IFluidContainerItem].fill(toFill, bl.drain(world, pos, true), true)
        }
        return true
      }
    } else {
      //todo: do we still need this?
      val bState = world.getBlockState(pos)
      if (bState.getBlock.getMaterial(bState) == Material.WATER && bState.getValue(BlockLiquid.LEVEL) == 0) {
        val ns = new FluidStack(FluidRegistry.WATER, 1000)
        val toFill = findFillTarget(ns, player.inventory, true)
        if (toFill != null) {
          if (!world.isRemote) {
            world.setBlockToAir(pos)
            toFill.getItem.asInstanceOf[IFluidContainerItem].fill(toFill, ns, true)
          }
          return true
        }
      } else if (bState.getBlock.getMaterial(bState) == Material.LAVA && bState.getValue(BlockLiquid.LEVEL) == 0) {
        val ns = new FluidStack(FluidRegistry.LAVA, 1000)
        val toFill = findFillTarget(ns, player.inventory, true)
        if (toFill != null) {
          if (!world.isRemote) {
            world.setBlockToAir(pos)
            toFill.getItem.asInstanceOf[IFluidContainerItem].fill(toFill, ns, true)
          }
          return true
        }
      } else {
        val te = world.getTileEntity(pos)
        if (te != null && te.isInstanceOf[IFluidHandler]) {
          if (!world.isRemote) {
            val fh = te.asInstanceOf[IFluidHandler]
            val fs = fh.drain(dir, maxDrain, false)
            val toFill = findFillTarget(fs, player.inventory, false)
            if (toFill != null) {
              val fci = toFill.getItem.asInstanceOf[IFluidContainerItem]
              val canFill = fci.fill(toFill, fs, false)
              if (canFill > 0) {
                fci.fill(toFill, fh.drain(dir, canFill, true), true)
                return true
              }
            }
          } else return true
        }
      }
    }
    return false
  }

  override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
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
