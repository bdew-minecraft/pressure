/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.sensor

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.gui.{DrawTarget, Rect}
import net.bdew.lib.multiblock.data.OutputConfigFluid
import net.bdew.lib.multiblock.interact.CIOutputFaces
import net.bdew.lib.sensors.multiblock.SensorOutput
import net.minecraft.tileentity.TileEntity

case class SensorOutputFlow(uid: String, iconName: String) extends Sensors.SensorType with Icons.Loader with SensorOutput {
  override def system = Sensors

  def getResultFromOutput(te: CIOutputFaces, output: Int) = {
    te.outputConfig.get(output) exists {
      case cfg: OutputConfigFluid =>
        cfg.avg > 0.1
      case _ => false
    }
  }

  @SideOnly(Side.CLIENT)
  override def drawSensor(rect: Rect, target: DrawTarget, obj: TileEntity): Unit = target.drawTexture(rect, texture)

}
