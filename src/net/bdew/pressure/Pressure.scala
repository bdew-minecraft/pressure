/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure

import java.io.File

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event._
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.relauncher.Side
import net.bdew.lib.Misc
import net.bdew.pressure.config._
import net.bdew.pressure.fmp.FmpHandler
import net.bdew.pressure.items.{Canister, CanisterRenderer}
import net.minecraftforge.client.MinecraftForgeClient
import org.apache.logging.log4j.Logger

@Mod(modid = Pressure.modId, version = "PRESSURE_VER", name = "Pressure Piping", dependencies = "after:ForgeMultipart;required-after:bdlib", modLanguage = "scala")
object Pressure {
  var log: Logger = null
  var instance = this

  final val modId = "pressure"
  final val channel = "bdew.pressure"

  var configDir: File = null

  def logInfo(msg: String, args: Any*) = log.info(msg.format(args: _*))
  def logWarn(msg: String, args: Any*) = log.warn(msg.format(args: _*))
  def logError(msg: String, args: Any*) = log.error(msg.format(args: _*))
  def logWarnException(msg: String, t: Throwable, args: Any*) = log.warn(msg.format(args: _*), t)
  def logErrorException(msg: String, t: Throwable, args: Any*) = log.error(msg.format(args: _*), t)

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    log = event.getModLog
    configDir = event.getModConfigurationDirectory
    TuningLoader.load("config")
    TuningLoader.load("override", false)
    Config.load(event.getSuggestedConfigurationFile)
    if (Misc.haveModVersion("ForgeMultipart")) new FmpHandler()
    if (event.getSide == Side.CLIENT) {
      MinecraftForgeClient.registerItemRenderer(Canister, CanisterRenderer)
    }
  }

  @EventHandler
  def init(event: FMLInitializationEvent) {
    NetworkRegistry.INSTANCE.registerGuiHandler(this, Config.guiHandler)
    TuningLoader.loadDealayed()
  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) {
  }
}