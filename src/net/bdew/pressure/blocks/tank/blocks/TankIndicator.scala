/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import cpw.mods.fml.client.registry.{ISimpleBlockRenderingHandler, RenderingRegistry}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.block.BlockRef
import net.bdew.lib.multiblock.tile.{TileController, TileModule}
import net.bdew.lib.render.connected.ConnectedHelper.{EdgeDraw, RectF, Vec3F}
import net.bdew.lib.render.connected.ConnectedRenderer
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.bdew.pressure.blocks.tank.{BaseModule, ModuleNeedsRenderUpdate}
import net.minecraft.block.Block
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.client.renderer.{RenderBlocks, Tessellator}
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection

object BlockTankIndicator extends BaseModule("TankIndicator", "TankBlock", classOf[TileTankIndicator]) with ModuleNeedsRenderUpdate {
  private def scanCollumn(r: Range, core: TileController, world: IBlockAccess, x: Int, z: Int, face: ForgeDirection) = r.view.map { yScan =>
    BlockRef(x, yScan, z).getTile[TileTankIndicator](world)
  } prefixLength { tile =>
    tile.isDefined &&
      tile.get.getCore.contains(core) &&
      shouldSideBeRendered(world, x + face.offsetX, tile.get.yCoord, z + face.offsetZ, face.ordinal())
  }

  override def getRenderType = TankIndicatorRenderer.id

  def getPositionInColumn(world: IBlockAccess, x: Int, y: Int, z: Int, face: ForgeDirection) = {
    getTE(world, x, y, z).getCore map { core =>
      val below = scanCollumn(y - 1 until(0, -1), core, world, x, z, face)
      val above = scanCollumn(y + 1 until world.getHeight, core, world, x, z, face)
      (below, above)
    } getOrElse ((0, 0))
  }

  override def getIcon(side: Int, meta: Int) =
    if (side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal())
      blockIcon
    else
      iconSingle

  override def getIcon(w: IBlockAccess, x: Int, y: Int, z: Int, side: Int) =
    if (side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal())
      blockIcon
    else
      getPositionInColumn(w, x, y, z, Misc.forgeDirection(side)) match {
        case (0, 0) => iconSingle
        case (b, 0) if b > 0 => iconUp
        case (0, a) if a > 0 => iconDown
        case _ => iconFull
      }

  var iconFull: IIcon = null
  var iconSingle: IIcon = null
  var iconUp: IIcon = null
  var iconDown: IIcon = null

  @SideOnly(Side.CLIENT)
  override def regIcons(ir: IIconRegister) {
    iconFull = ir.registerIcon("pressure:tank/" + name.toLowerCase + "/full")
    iconSingle = ir.registerIcon("pressure:tank/" + name.toLowerCase + "/single")
    iconUp = ir.registerIcon("pressure:tank/" + name.toLowerCase + "/up")
    iconDown = ir.registerIcon("pressure:tank/" + name.toLowerCase + "/down")
  }
}

class TileTankIndicator extends TileModule {
  val kind: String = "TankBlock"
}

object TankIndicatorRenderer extends ISimpleBlockRenderingHandler {
  val id = RenderingRegistry.getNextAvailableRenderId
  RenderingRegistry.registerBlockHandler(this)

  override def getRenderId = id
  override def shouldRender3DInInventory(modelId: Int) = true

  override def renderInventoryBlock(block: Block, metadata: Int, modelId: Int, renderer: RenderBlocks) =
    ConnectedRenderer.renderInventoryBlock(block, metadata, modelId, renderer)

  override def renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks) = {
    ConnectedRenderer.renderWorldBlock(world, x, y, z, block, modelId, renderer)
    BlockTankIndicator.getTE(world, x, y, z).getCoreAs[TileTankController] map { core =>
      if (core.tank.getFluid != null && core.tank.getFluidAmount > 0) {
        for (face <- Array(ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST)) {
          if (block.shouldSideBeRendered(world, x + face.offsetX, y + face.offsetY, z + face.offsetZ, face.ordinal())) {
            val (below, above) = BlockTankIndicator.getPositionInColumn(world, x, y, z, face)
            val low = if (below > 0) 0F else 0.125F
            val high = if (above > 0) 1F else 0.875F
            val span = high - low
            val blockVal = core.tank.getCapacity.toFloat / (above + below + 1)
            val myFluid = Misc.clamp(core.tank.getFluidAmount.toFloat - below * blockVal, 0F, blockVal) / blockVal
            Tessellator.instance.setColorOpaque_I(core.tank.getFluid.getFluid.getColor(core.tank.getFluid))
            new EdgeDraw(RectF(7 / 16F, low, 9 / 16F, low + span * myFluid), face).doDraw(Vec3F(x, y, z), core.tank.getFluid.getFluid.getIcon)
            Tessellator.instance.setColorOpaque_F(1, 1, 1)
          }
        }
      }
    }
    true
  }
}