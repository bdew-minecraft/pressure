/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.bdew.lib.items.SimpleItem
import net.bdew.pressure.config.Tuning
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action
import net.minecraftforge.fluids._

object HandPump extends SimpleItem("HandPump") {
  lazy val cfg = Tuning.getSection("Items").getSection(name)
  lazy val maxDrain = cfg.getInt("MaxDrain")

  setMaxStackSize(1)
  MinecraftForge.EVENT_BUS.register(this)

  def findFillTarget(fs: FluidStack, inventory: IInventory, mustTakeAll: Boolean): ItemStack = {
    if (fs == null) return null
    for (i <- 0 until inventory.getSizeInventory) {
      val item = inventory.getStackInSlot(i)
      if (item != null && item.getItem != null && item.getItem.isInstanceOf[IFluidContainerItem]) {
        val fc = item.getItem.asInstanceOf[IFluidContainerItem]
        val canFill = fc.fill(item, fs, false)
        if ((mustTakeAll && canFill == fs.amount) || (!mustTakeAll && canFill > 0)) return item
      }
    }
    return null
  }

  def drainBlock(world: World, block: Block, x: Int, y: Int, z: Int, stack: ItemStack, dir: ForgeDirection, player: EntityPlayer): Boolean = {
    if (block.isInstanceOf[BlockFluidBase]) {
      val bl = block.asInstanceOf[BlockFluidBase]
      val fl = bl.drain(world, x, y, z, false)
      val toFill = findFillTarget(fl, player.inventory, true)
      if (toFill != null) {
        if (!world.isRemote) {
          toFill.getItem.asInstanceOf[IFluidContainerItem].fill(toFill, bl.drain(world, x, y, z, true), true)
        }
        return true
      }
    } else if (world.getBlock(x, y, z).getMaterial == Material.water && world.getBlockMetadata(x, y, z) == 0) {
      val ns = new FluidStack(FluidRegistry.WATER, 1000)
      val toFill = findFillTarget(ns, player.inventory, true)
      if (toFill != null) {
        if (!world.isRemote) {
          world.setBlockToAir(x, y, z)
          toFill.getItem.asInstanceOf[IFluidContainerItem].fill(toFill, ns, true)
        }
        return true
      }
    } else if (world.getBlock(x, y, z).getMaterial == Material.lava && world.getBlockMetadata(x, y, z) == 0) {
      val ns = new FluidStack(FluidRegistry.LAVA, 1000)
      val toFill = findFillTarget(ns, player.inventory, true)
      if (toFill != null) {
        if (!world.isRemote) {
          world.setBlockToAir(x, y, z)
          toFill.getItem.asInstanceOf[IFluidContainerItem].fill(toFill, ns, true)
        }
        return true
      }
    } else {
      val te = world.getTileEntity(x, y, z)
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
    return false
  }

  override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
    if (player.isSneaking) return stack
    val mop = getMovingObjectPositionFromPlayer(world, player, true)
    if (mop == null) return stack

    val block = Option(world.getBlock(mop.blockX, mop.blockY, mop.blockZ)).getOrElse(return stack)

    if (drainBlock(world, block, mop.blockX, mop.blockY, mop.blockZ, stack, ForgeDirection.values()(mop.sideHit).getOpposite, player)) {
      if (!world.isRemote)
        player.inventoryContainer.detectAndSendChanges()
      player.swingItem()
      player.playSound("random.drink", 0.5F, world.rand.nextFloat * 0.1F + 0.9F)
      return stack
    }

    return stack
  }

  @SubscribeEvent
  def onInteract(ev: PlayerInteractEvent) {
    val item = ev.entityPlayer.getHeldItem
    if (ev.action == Action.RIGHT_CLICK_BLOCK && item != null && item.getItem == this && !ev.entityPlayer.isSneaking) ev.setCanceled(true)
  }
}
