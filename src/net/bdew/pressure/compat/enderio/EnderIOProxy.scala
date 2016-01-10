/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.enderio

import net.bdew.lib.Misc
import net.bdew.pressure.Pressure
import net.bdew.pressure.api.IFilterableProvider
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

import scala.language.reflectiveCalls

object EnderIOProxy extends IFilterableProvider {
  // Todo: EnderIO is not out for 1.8, this will probably need tweaking.

  def init() {
    try {
      Pressure.logInfo("Loading EnderIO compatibility...")
      Pressure.logInfo("TCB=%s", EnderIOReflect.clsTileConduitBundle)
      Pressure.logInfo("LC=%s", EnderIOReflect.clsLiquidConduit)
      Pressure.logInfo("ALC=%s", EnderIOReflect.clsAdvancedLiquidConduit)
      Helper.registerIFilterableProvider(this)
    } catch {
      case e: Throwable => Pressure.logErrorException("Error in EnderIO proxy", e)
    }
  }

  override def getFilterableForWorldCoordinates(world: World, pos: BlockPos, side: EnumFacing) = {
    import net.bdew.pressure.compat.enderio.EnderIOReflect._
    (Option(world.getTileEntity(pos)) flatMap Misc.asInstanceOpt(clsTileConduitBundle) map { te =>
      if (te.getConduit(clsLiquidConduit) != null) new FilterableProxy(te, clsLiquidConduit)
      else if (te.getConduit(clsAdvancedLiquidConduit) != null) new FilterableProxy(te, clsAdvancedLiquidConduit)
      else null
    }).orNull
  }
}
