/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.controller

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.pressure.Pressure
import net.bdew.pressure.blocks.tank.BaseController
import net.minecraft.block.Block
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

object BlockTankController extends BaseController("TankController", classOf[TileTankController]) {
  var topIcon: IIcon = null
  var bottomIcon: IIcon = null

  override def breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, meta: Int) {
    if (!world.isRemote)
      getTE(world, x, y, z).dropItems()
    super.breakBlock(world, x, y, z, block, meta)
  }

  @SideOnly(Side.CLIENT)
  override def regIcons(ir: IIconRegister) {
    topIcon = ir.registerIcon(Misc.iconName(Pressure.modId, "tank", name, "top"))
    bottomIcon = ir.registerIcon(Misc.iconName(Pressure.modId, "tank", name, "bottom"))
  }

  override def getIcon(side: Int, meta: Int) =
    if (side == ForgeDirection.UP.ordinal())
      topIcon
    else if (side == ForgeDirection.DOWN.ordinal())
      bottomIcon
    else
      blockIcon

}
