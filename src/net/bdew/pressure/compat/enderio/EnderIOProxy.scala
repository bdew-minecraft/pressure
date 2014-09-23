/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.compat.enderio

import net.bdew.pressure.Pressure
import net.bdew.pressure.api.IFilterableProvider
import net.bdew.pressure.misc.Helper
import net.minecraft.world.World

object EnderIOProxy extends IFilterableProvider {
  def init() {
    try {
      Pressure.logInfo("Loading EnderIO compatibility...")
      Pressure.logInfo("TCB=%s", EnderIOReflect.cTCB)
      Pressure.logInfo("LC=%s", EnderIOReflect.cLC)
      Pressure.logInfo("LCN=%s", EnderIOReflect.cLCN)
      Pressure.logInfo("ALC=%s", EnderIOReflect.cALC)
      Pressure.logInfo("ALCN=%s", EnderIOReflect.cALCN)
      Helper.registerIFilterableProvider(this)
    } catch {
      case e: Throwable => Pressure.logErrorException("Error in EnderIO proxy", e)
    }
  }

  override def getFilterableForWorldCoords(world: World, x: Int, y: Int, z: Int, side: Int) = {
    import net.bdew.pressure.compat.enderio.EnderIOReflect._
    val te = world.getTileEntity(x, y, z)
    if (te != null && cTCB.isInstance(te)) {
      if (mTCBgetConduit.invoke(te, cLC) != null) new FilterableProxyLC(te)
      else if (mTCBgetConduit.invoke(te, cALC) != null) new FilterableProxyALC(te)
      else null
    } else null
  }
}
