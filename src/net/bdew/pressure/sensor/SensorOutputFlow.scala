/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.sensor

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.gui.{DrawTarget, Rect, Texture}
import net.bdew.lib.multiblock.data.OutputConfigFluid
import net.bdew.lib.multiblock.interact.CIOutputFaces
import net.bdew.lib.render.connected.BlockAdditionalRender
import net.bdew.lib.sensors.GenericSensorParameter
import net.bdew.pressure.PressureResourceProvider
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

case class SensorOutputFlowParameter(output: Int) extends Sensors.SensorParameter {
  override val uid: String = "output." + output
  override def localizedName: String = Misc.toLocal(PressureResourceProvider.unlocalizedOutputName(output))
}

case class SensorOutputFlow(uid: String, iconName: String) extends Sensors.SensorType with Icons.Loader {
  override def defaultParameter = Sensors.DisabledParameter

  override def loadParameter(tag: NBTTagCompound) = {
    if (tag.hasKey("output"))
      SensorOutputFlowParameter(tag.getInteger("output"))
    else
      Sensors.DisabledParameter
  }

  override def saveParameter(p: GenericSensorParameter, tag: NBTTagCompound): Unit = p match {
    case SensorOutputFlowParameter(n) => tag.setInteger("output", n)
    case _ =>
  }

  override def isValidParameter(p: GenericSensorParameter, obj: TileEntity): Boolean = (p, obj) match {
    case (SensorOutputFlowParameter(n), x: CIOutputFaces) => x.outputFaces.inverted.isDefinedAt(n)
    case (Sensors.DisabledParameter, _) => true
    case _ => false
  }

  override def getResult(param: GenericSensorParameter, obj: TileEntity): Boolean = (param, obj) match {
    case (SensorOutputFlowParameter(n), x: CIOutputFaces) =>
      x.outputConfig.get(n) exists {
        case cfg: OutputConfigFluid =>
          cfg.avg > 0.1
        case _ => false
      }
    case _ => false
  }

  override def paramClicked(current: GenericSensorParameter, item: ItemStack, button: Int, mod: Int, obj: TileEntity) =
    if (mod == 0 && (button == 0 || button == 1) && item == null)
      (current, obj) match {
        case (SensorOutputFlowParameter(n), x: CIOutputFaces) =>
          val outputs = x.outputFaces.map.values.toList.sorted
          if (outputs.size > 0)
            if (button == 0)
              SensorOutputFlowParameter(Misc.nextInSeq(outputs, n))
            else
              SensorOutputFlowParameter(Misc.prevInSeq(outputs, n))
          else
            Sensors.DisabledParameter
        case (_, x: CIOutputFaces) =>
          if (button == 0)
            x.outputFaces.map.values.toList.sorted.headOption map SensorOutputFlowParameter getOrElse Sensors.DisabledParameter
          else
            x.outputFaces.map.values.toList.sorted.reverse.headOption map SensorOutputFlowParameter getOrElse Sensors.DisabledParameter
        case _ =>
          Sensors.DisabledParameter
      }
    else current

  @SideOnly(Side.CLIENT)
  override def drawParameter(rect: Rect, target: DrawTarget, obj: TileEntity, param: GenericSensorParameter): Unit = (param, obj) match {
    case (SensorOutputFlowParameter(output), te: CIOutputFaces) =>
      val faces = te.outputFaces.inverted
      if (faces.isDefinedAt(output)) {
        val bf = faces(output)
        bf.origin.block(te.getWorldObj) foreach { block =>
          target.drawTexture(rect, Texture(Texture.BLOCKS, block.getIcon(te.getWorldObj, bf.origin.x, bf.origin.y, bf.origin.z, bf.face.ordinal())))
          if (block.isInstanceOf[BlockAdditionalRender]) {
            for (over <- block.asInstanceOf[BlockAdditionalRender].getFaceOverlays(te.getWorldObj, bf.origin.x, bf.origin.y, bf.origin.z, bf.face))
              target.drawTexture(rect, Texture(Texture.BLOCKS, over.icon), over.color)
          }
        }
      }

    case _ => target.drawTexture(rect, Sensors.DisabledParameter.texture, Sensors.DisabledParameter.textureColor)
  }

  override def getParamTooltip(obj: TileEntity, param: GenericSensorParameter) = (param, obj) match {
    case (SensorOutputFlowParameter(output), te: CIOutputFaces) =>
      val faces = te.outputFaces.inverted
      var list = List.empty[String]
      list :+= Misc.toLocal(te.resources.unlocalizedOutputName(output))
      if (faces.isDefinedAt(output)) {
        val bf = faces(output)
        bf.origin.block(te.getWorldObj) foreach { block =>
          list :+= block.getLocalizedName
          list :+= "%d, %d, %d - %s".format(bf.x, bf.y, bf.z, Misc.toLocal("bdlib.multiblock.face." + bf.face.toString.toLowerCase))
        }
      }
      list
    case _ => super.getParamTooltip(obj, param)
  }

  @SideOnly(Side.CLIENT)
  override def drawSensor(rect: Rect, target: DrawTarget, obj: TileEntity): Unit = target.drawTexture(rect, texture)
}
