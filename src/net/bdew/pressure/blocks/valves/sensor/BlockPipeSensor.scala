/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.valves.sensor

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.bdew.pressure.blocks.valves.BlockValve
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.world.IBlockAccess

object BlockPipeSensor extends SimpleBlock("PipeSensor", Material.iron) with HasTE[TilePipeSensor] with BlockValve {
  override val TEClass = classOf[TilePipeSensor]

  setHardness(2)

  override def canProvidePower = true

  override def isProvidingWeakPower(w: IBlockAccess, x: Int, y: Int, z: Int, side: Int) = {
    val facing = getFacing(w, x, y, z)
    if (facing.ordinal() != side && facing.getOpposite.ordinal() != side && isPowered(w, x, y, z))
      15
    else
      0
  }

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(ir: IIconRegister) = {
    frontIcon = ir.registerIcon("pressure:%s/front".format(name.toLowerCase))
    sideIconOn = ir.registerIcon("pressure:%s/side_on".format(name.toLowerCase))
    sideIconOff = ir.registerIcon("pressure:%s/side_off".format(name.toLowerCase))
  }
}
