/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/pressure/master/MMPL-1.0.txt
 */

package net.bdew.pressure.config

import net.bdew.lib.config.BlockManager
import net.bdew.pressure.blocks._
import net.bdew.pressure.blocks.pump.BlockPump
import net.bdew.pressure.blocks.input.BlockInput
import net.bdew.pressure.blocks.output.BlockOutput
import net.bdew.pressure.blocks.source.BlockWaterSource

object Blocks extends BlockManager(Config.IDs) {
  val pipe = regBlockCls(classOf[BlockPipe], "Pipe")
  val pump = regBlockCls(classOf[BlockPump], "Pump")
  val output = regBlockCls(classOf[BlockOutput], "Output")
  val input = regBlockCls(classOf[BlockInput], "Input")
  val water = regBlockCls(classOf[BlockWaterSource], "Water")
}