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
import net.bdew.pressure.compat.computercraft.{CallContext, TileCommandHandler, TilePeripheralProvider}

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
    res(ctx.tile.getCore.isDefined)
  }

  command("hasFluid") { ctx =>
    val tank = getCore(ctx).tank
    res(tank.getFluid != null && tank.getFluid.amount > 0)
  }

  command("getFluidType") { ctx =>
    val fluid = getCore(ctx).tank.getFluid
    if (fluid != null && fluid.getFluid != null)
      res(fluid.getFluid.getName)
    else
      Array(null)
  }

  command("getFluidAmount") { ctx =>
    val fluid = getCore(ctx).tank.getFluid
    if (fluid != null)
      res(fluid.amount)
    else
      res(0)
  }

  command("getCapacity") { ctx =>
    res(getCore(ctx).tank.getCapacity)
  }

  command("isValidOutput") { ctx =>
    ctx.params match {
      case Array(oName: String) =>
        outputNames.get(oName.toLowerCase) flatMap getCore(ctx).outputConfig.get match {
          case Some(_) => res(true)
          case None => res(false)
        }
      case _ => err("Usage: isValidOutput(string)")
    }
  }

  command("getOutputMode") { ctx =>
    ctx.params match {
      case Array(oName: String) =>
        val oNum = outputNames.getOrElse(oName.toLowerCase, err("Invalid output name"))
        getCore(ctx).outputConfig.get(oNum) match {
          case Some(x: OutputConfigRSControllable) => res(x.rsMode.toString)
          case _ => err("Unable to get output mode")
        }
      case _ => err("Usage: getOutputMode(string)")
    }
  }

  command("setOutputMode") { ctx =>
    ctx.params match {
      case Array(oName: String, mode: String) =>
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
            res(true)
          case _ => err("Unable to set output state")
        }
      case _ => err("Usage: setOutputMode(string, string)")
    }
  }

}

