/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.config

import java.io.{File, FileReader, InputStreamReader}

import net.bdew.lib.recipes.gencfg._
import net.bdew.lib.recipes.{RecipeLoader, RecipeParser}
import net.bdew.pressure.Pressure

object Tuning extends ConfigSection

object TuningLoader {

  class Parser extends RecipeParser with GenericConfigParser

  class Loader extends RecipeLoader with GenericConfigLoader {
    val cfgStore = Tuning

    override def newParser() = new Parser()
  }

  val loader = new Loader

  def loadDealayed() = loader.processDelayedStatements()

  def load(part: String, checkJar: Boolean = true) {
    val f = new File(Pressure.configDir, "%s-%s.cfg".format(Pressure.modId, part))
    val r = if (f.exists() && f.canRead) {
      Pressure.logInfo("Loading configuration from %s", f.toString)
      new FileReader(f)
    } else if (checkJar) {
      val res = "/assets/%s/%s-%s.cfg".format(Pressure.modId, Pressure.modId, part)
      val stream = this.getClass.getResourceAsStream(res)
      Pressure.logInfo("Loading configuration from JAR - %s", this.getClass.getResource(res))
      new InputStreamReader(this.getClass.getResourceAsStream("/assets/%s/%s-%s.cfg".format(Pressure.modId, Pressure.modId, part)))
    } else {
      return
    }
    try {
      loader.load(r)
    } finally {
      r.close()
    }
  }
}

