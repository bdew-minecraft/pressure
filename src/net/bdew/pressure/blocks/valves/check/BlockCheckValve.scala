/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves.check

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.bdew.pressure.blocks.valves.BlockValve
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object BlockCheckValve extends SimpleBlock("CheckValve", Material.iron) with HasTE[TileCheckValve] with BlockValve {
  override val TEClass = classOf[TileCheckValve]

  setHardness(2)

  override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, block: Block) {
    val meta = world.getBlockMetadata(x, y, z)
    val powered = world.isBlockIndirectlyGettingPowered(x, y, z)
    if (powered && ((meta & 8) == 0))
      world.setBlockMetadataWithNotify(x, y, z, (meta & 7) | 8, 2)
    else if (!powered && ((meta & 8) == 8))
      world.setBlockMetadataWithNotify(x, y, z, meta & 7, 2)
  }

  override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, ent: EntityLivingBase, stack: ItemStack): Unit = {
    super.onBlockPlacedBy(world, x, y, z, ent, stack)
    onNeighborBlockChange(world, x, y, z, this)
  }

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(ir: IIconRegister) = {
    frontIcon = ir.registerIcon("pressure:%s/front".format(name.toLowerCase))
    sideIconOn = ir.registerIcon("pressure:%s/side_on".format(name.toLowerCase))
    sideIconOff = ir.registerIcon("pressure:%s/side_off".format(name.toLowerCase))
  }
}
