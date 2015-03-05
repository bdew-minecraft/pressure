/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.director

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.bdew.pressure.Pressure
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

object BlockDirector extends SimpleBlock("Director", Material.iron) with HasTE[TileDirector] {
  val cfg = MachineDirector

  override val TEClass = classOf[TileDirector]

  override def getRenderType = DirectorRenderer.id

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, meta: Int, xOffs: Float, yOffs: Float, zOffs: Float): Boolean = {
    if (player.isSneaking) return false
    if (world.isRemote) return true
    player.openGui(Pressure, cfg.guiId, world, x, y, z)
    true
  }

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(reg: IIconRegister) {
    blockIcon = reg.registerIcon(modId + ":" + name.toLowerCase + "/main")
  }
}
