/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.Misc
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.lib.render.BaseBlockRenderHandler
import net.bdew.lib.render.connected.ConnectedHelper.Vec3F
import net.bdew.lib.render.connected.ConnectedRenderer
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.bdew.pressure.blocks.tank.{BaseModule, MIFilterable, ModuleNeedsRenderUpdate}
import net.bdew.pressure.items.configurator.ItemConfigurator
import net.bdew.pressure.render.FilterableBlockRenderer
import net.minecraft.block.Block
import net.minecraft.client.renderer.{RenderBlocks, Tessellator}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

object BlockTankFilter extends BaseModule("TankFilter", "FluidFilter", classOf[TileTankFilter]) with ModuleNeedsRenderUpdate {
  override def getRenderType = TankFilterRenderer.id
  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, meta: Int, xOffs: Float, yOffs: Float, zOffs: Float) = {
    if (player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem == ItemConfigurator)
      false // Let the configurator handle the click
    else
      super.onBlockActivated(world, x, y, z, player, meta, xOffs, yOffs, zOffs)
  }
}

class TileTankFilter extends TileModule with MIFilterable {
  val kind: String = "FluidFilter"
}

object TankFilterRenderer extends BaseBlockRenderHandler {
  override def renderInventoryBlock(block: Block, metadata: Int, modelId: Int, renderer: RenderBlocks) =
    ConnectedRenderer.renderInventoryBlock(block, metadata, modelId, renderer)

  override def renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks) = {
    ConnectedRenderer.renderWorldBlock(world, x, y, z, block, modelId, renderer)
    for {
      core <- BlockTankFilter.getTE(world, x, y, z).getCoreAs[TileTankController]
      fluid <- core.getFluidFilter
      face <- ForgeDirection.VALID_DIRECTIONS
      if block.shouldSideBeRendered(world, x + face.offsetX, y + face.offsetY, z + face.offsetZ, face.ordinal())
    } {
      Tessellator.instance.setColorOpaque_I(Misc.getFluidColor(fluid))
      FilterableBlockRenderer.filterIconDraw(face).doDraw(Vec3F(x, y, z), Misc.getFluidIcon(fluid))
      Tessellator.instance.setColorOpaque_F(1, 1, 1)
    }
    true
  }
}