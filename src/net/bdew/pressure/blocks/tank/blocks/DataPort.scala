/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.tank.blocks

import dan200.computercraft.api.ComputerCraftAPI
import net.bdew.lib.multiblock.data.{OutputConfigRSControllable, RSMode}
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.pressure.blocks.tank.BaseModule
import net.bdew.pressure.blocks.tank.controller.TileTankController
import net.bdew.pressure.compat.computercraft._

object BlockDataport extends BaseModule("TankDataPort", "TankDataPort", classOf[TileDataport]) {
  ComputerCraftAPI.registerPeripheralProvider(new TilePeripheralProvider("tank_dataport", DataportCommands, classOf[TileDataport]))
}

class TileDataport extends TileModule {
  val kind: String = "TankDataPort"

  override def getCore = getCoreAs[TileTankController]
}

object DataportCommands extends TileCommandHandler[TileDataport] {
  val outputNames = Map(
    "red" -> 0,
    "green" -> 1,
    "blue" -> 2,
    "yellow" -> 3,
    "cyan" -> 4,
    "purple" -> 5
  )

  def getCore(ctx: CallContext[TileDataport]) =
    ctx.tile.getCore.getOrElse(err("Not connected to tank"))

  command("isConnected") { ctx =>
    ctx.tile.getCore.isDefined
  }

  command("hasFluid") { ctx =>
    val tank = getCore(ctx).tank
    tank.getFluid != null && tank.getFluid.amount > 0
  }

  command("getFluidType") { ctx =>
    val fluid = getCore(ctx).tank.getFluid
    if (fluid != null && fluid.getFluid != null)
      fluid.getFluid.getName
    else
      CCResult.Null
  }

  command("getFluidAmount") { ctx =>
    val fluid = getCore(ctx).tank.getFluid
    if (fluid != null)
      fluid.amount
    else
      0
  }

  command("getCapacity") { ctx =>
    getCore(ctx).tank.getCapacity
  }

  command("isValidOutput") { ctx =>
    val oName = ctx.params(CCString)
    outputNames.get(oName.toLowerCase) flatMap getCore(ctx).outputConfig.get match {
      case Some(_) => true
      case None => false
    }
  }

  command("getOutputMode") { ctx =>
    val oName = ctx.params(CCString)
    val oNum = outputNames.getOrElse(oName.toLowerCase, err("Invalid output name"))
    getCore(ctx).outputConfig.get(oNum) match {
      case Some(x: OutputConfigRSControllable) => x.rsMode.toString
      case _ => err("Unable to get output mode")
    }
  }

  command("setOutputMode") { ctx =>
    val (oName, mode) = ctx.params(CCString, CCString)
    val oNum = outputNames.getOrElse(oName.toLowerCase, err("Invalid output name"))
    val newMode = try {
      RSMode.withName(mode.toUpperCase)
    } catch {
      case e: NoSuchElementException => err("Invalid output mode")
    }
    getCore(ctx).outputConfig.get(oNum) match {
      case Some(x: OutputConfigRSControllable) =>
        x.rsMode = newMode
        getCore(ctx).outputConfig.updated()
        true
      case _ => err("Unable to set output state")
    }
  }
}

