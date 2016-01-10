/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.multiblock.tile.{TileController, TileModule}
import net.bdew.pressure.blocks.tank.{BaseModule, ModuleNeedsRenderUpdate}
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.IBlockAccess

object BlockTankIndicator extends BaseModule("TankIndicator", "TankBlock", classOf[TileTankIndicator]) with ModuleNeedsRenderUpdate {
  private def scanColumn(r: Seq[BlockPos], core: TileController, world: IBlockAccess, face: EnumFacing) = {
    r.view.map { p =>
      world.getTileSafe[TileTankIndicator](p)
    } prefixLength { tileOpt =>
      tileOpt exists { tile =>
        tile.getCore.contains(core) && shouldSideBeRendered(world, tile.getPos.offset(face), face)
      }
    }
  }

  def getPositionInColumn(world: IBlockAccess, pos: BlockPos, face: EnumFacing) = {
    getTE(world, pos).getCore map { core =>
      val below = scanColumn(pos.down() to pos.copy(y = 1), core, world, face)
      val above = scanColumn(pos.up() to pos.copy(y = 255), core, world, face)
      (below, above)
    } getOrElse ((0, 0))
  }
}

class TileTankIndicator extends TileModule {
  val kind: String = "TankBlock"
}