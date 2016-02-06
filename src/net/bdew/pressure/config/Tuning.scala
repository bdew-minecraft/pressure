/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.config

import java.io.{File, FileWriter}

import net.bdew.lib.recipes.gencfg._
import net.bdew.lib.recipes.{RecipeLoader, RecipeParser, RecipesHelper}
import net.bdew.pressure.Pressure

object Tuning extends ConfigSection

object TuningLoader {

  class Parser extends RecipeParser with GenericConfigParser

  class Loader extends RecipeLoader with GenericConfigLoader {
    val cfgStore = Tuning

    override def newParser() = new Parser()
  }

  val loader = new Loader

  def loadDelayed() = loader.processRecipeStatements()

  def loadConfigFiles() {
    if (!Pressure.configDir.exists()) {
      Pressure.configDir.mkdir()
      val nl = System.getProperty("line.separator")
      val f = new FileWriter(new File(Pressure.configDir, "readme.txt"))
      f.write("Any .cfg files in this directory will be loaded after the internal configuration, in alphabetic order" + nl)
      f.write("Files in 'overrides' directory with matching names cab be used to override internal configuration" + nl)
      f.close()
    }

    RecipesHelper.loadConfigs(
      modName = "Pressure Pipes",
      listResource = "/assets/pressure/config/files.lst",
      configDir = Pressure.configDir,
      resBaseName = "/assets/pressure/config/",
      loader = loader)

    if (1.0 * Modules.TankBlock.capacity * Tuning.getSection("Machines").getSection("TankController").getSection("Modules").getInt("TankBlock") > Int.MaxValue) {
      Pressure.logWarn("Current configuration allows building tanks larger than %d mb which is known to cause issues, all tanks will be capped to that capacity", Int.MaxValue)
    }
  }
}

