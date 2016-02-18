/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure

import java.io.File

import net.bdew.lib.Misc
import net.bdew.pressure.api.PressureAPI
import net.bdew.pressure.blocks.router.BlockRouter
import net.bdew.pressure.compat.computercraft.CCBlocks
import net.bdew.pressure.compat.enderio.EnderIOProxy
import net.bdew.pressure.compat.opencomputers.OCBlocks
import net.bdew.pressure.config._
import net.bdew.pressure.misc.PressureCreativeTabs
import net.bdew.pressure.mutilpart.MCMPHandler
import net.bdew.pressure.network.NetworkHandler
import net.bdew.pressure.pressurenet.Helper
import net.minecraft.item.Item
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.Logger

@Mod(modid = Pressure.modId, version = "PRESSURE_VER", name = "Pressure Pipes", dependencies = "after:ForgeMultipart;after:ComputerCraft;required-after:bdlib", modLanguage = "scala")
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
    PressureAPI.HELPER = Helper
    configDir = new File(event.getModConfigurationDirectory, "PressurePipes")
    TuningLoader.loadConfigFiles()
    Items.load()
    Blocks.load()
    Machines.load()
    if (event.getSide == Side.CLIENT) PressureClient.preInit()
  }

  @EventHandler
  def init(event: FMLInitializationEvent) {
    if (event.getSide.isClient) Config.load(new File(configDir, "client.config"))
    NetworkRegistry.INSTANCE.registerGuiHandler(this, Config.guiHandler)
    TuningLoader.loadDelayed()
    FMLInterModComms.sendMessage("Waila", "register", "net.bdew.pressure.waila.WailaHandler.loadCallback")
    if (Misc.haveModVersion("OpenComputers"))
      OCBlocks.init()
    if (Misc.haveModVersion("ComputerCraft"))
      CCBlocks.init()
    if (Misc.haveModVersion("mcmultipart"))
      MCMPHandler.init()
    NetworkHandler.init()
  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) {
    PressureCreativeTabs.init()
    if (Misc.haveModVersion("EnderIO"))
      EnderIOProxy.init()
  }

  @EventHandler
  def missingMappings(event: FMLMissingMappingsEvent): Unit = {
    import scala.collection.JavaConversions._
    for (missing <- event.getAll) {
      (missing.name, missing.`type`) match {
        case ("pressure:Director", GameRegistry.Type.BLOCK) => missing.remap(BlockRouter)
        case ("pressure:Director", GameRegistry.Type.ITEM) => missing.remap(Item.getItemFromBlock(BlockRouter))
        case _ => // do nothing
      }
    }
  }
}