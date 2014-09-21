/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.lib.Misc
import net.bdew.lib.block.BlockFace
import net.bdew.lib.multiblock.interact.MIOutput
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.minecraft.item.ItemStack

object WailaTankModuleProvider extends BaseDataProvider(classOf[TileModule]) {
  override def getBodyStrings(target: TileModule, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    (for {
      core <- target.getCore
      controller <- Misc.asInstanceOpt(core, classOf[TileTankController])
    } yield {
      var out = WailaTankProvider.getBodyStrings(controller, stack, acc, cfg)
      if (target.isInstanceOf[MIOutput[_]]) {
        controller.outputFaces get BlockFace(target.mypos, acc.getSide) map { output =>
          out :+= Misc.toLocal("pressure.output." + output)
        }
      }
      out
    }).getOrElse(List.empty)
  }
}
