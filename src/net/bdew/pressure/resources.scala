/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure

import net.bdew.lib.gui.{Color, ScaledResourceLocation, Texture}
import net.bdew.lib.multiblock.ResourceProvider
import net.bdew.lib.multiblock.data.RSMode

// todo: rendering stuff
//object IconCache extends IconPreloader {
//  val edgeIcon = TextureLoc("pressure:connected/edge")
//  val output = TextureLoc("pressure:connected/output")
//  val disabled = TextureLoc("pressure:connected/disabled")
//  val arTop = TextureLoc("pressure:connected/artop")
//  val arRight = TextureLoc("pressure:connected/arright")
//  val arBottom = TextureLoc("pressure:connected/arbottom")
//  val arLeft = TextureLoc("pressure:connected/arleft")
//}

object Textures {
  val sheet = new ScaledResourceLocation(Pressure.modId, "textures/gui/widgets.png")
  val tankOverlay = Texture(sheet, 0, 0, 16, 58)

  object Button16 {
    val base = Texture(sheet, 16, 0, 16, 16)
    val hover = Texture(sheet, 32, 0, 16, 16)
    val rsOn = Texture(sheet, 33, 17, 14, 14)
    val rsOff = Texture(sheet, 17, 17, 14, 14)
    val enabled = Texture(sheet, 49, 17, 14, 14)
    val disabled = Texture(sheet, 65, 17, 14, 14)
  }

  lazy val iconRSMode = Map(
    RSMode.ALWAYS -> Button16.enabled,
    RSMode.NEVER -> Button16.disabled,
    RSMode.RS_ON -> Button16.rsOn,
    RSMode.RS_OFF -> Button16.rsOff
  )

  def progress(width: Int) = Texture(sheet, 136 - width, 35, width, 16)
}

object PressureResourceProvider extends ResourceProvider {
  // todo: rendering stuff
  //  override def edge = IconCache.edgeIcon
  //  override def output = IconCache.output
  //  override def disabled = IconCache.disabled
  //
  //  override def arrowBottom = IconCache.arBottom
  //  override def arrowRight = IconCache.arRight
  //  override def arrowTop = IconCache.arTop
  //  override def arrowLeft = IconCache.arLeft

  override def btRsOff = Textures.Button16.rsOff
  override def btRsOn = Textures.Button16.rsOn
  override def btDisabled = Textures.Button16.disabled
  override def btEnabled = Textures.Button16.enabled

  override def btBase = Textures.Button16.base
  override def btHover = Textures.Button16.hover

  override val outputColors = Map(
    0 -> Color(1F, 0F, 0F),
    1 -> Color(0F, 1F, 0F),
    2 -> Color(0F, 0F, 1F),
    3 -> Color(1F, 1F, 0F),
    4 -> Color(0F, 1F, 1F),
    5 -> Color(1F, 0F, 1F)
  )
  override val unlocalizedOutputName = (outputColors.keys map (n => n -> "pressure.output.%d".format(n))).toMap

  override def getModuleName(s: String) = "pressure.module." + s + ".name"
}