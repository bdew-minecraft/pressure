/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.items.configurator

import java.util.Locale

import net.bdew.lib.gui._
import net.bdew.lib.gui.widgets._
import net.bdew.lib.{Client, Misc}
import net.bdew.pressure.network.NetworkHandler
import net.bdew.pressure.{Pressure, Textures}
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fluids.{Fluid, FluidRegistry, FluidStack}
import org.lwjgl.input.Keyboard

class GuiConfigurator(player: EntityPlayer) extends BaseScreen(new ContainerConfigurator(player), 176, 170) {
  val texture = new ScaledResourceLocation(Pressure.modId, "textures/gui/configurator.png")
  override val background = Texture(texture, rect)

  val searchIcon = Texture(texture, 208, 0, 48, 48)

  var searchEdit: WidgetTextEdit = null
  var allFluids = Vector.empty[(String, Fluid)]
  var displayMap = Map.empty[Int, Int]
  var lastSearch: String = null

  override def initGui() {
    super.initGui()
    Keyboard.enableRepeatEvents(true)

    widgets.add(new WidgetLabel(Misc.toLocal("container.inventory"), 8, 77, Color.darkGray))
    widgets.add(new WidgetSimpleIcon(new Rect(8, 4, 16, 16), searchIcon))

    searchEdit = widgets.add(new WidgetTextEdit(new Rect(26, 6, 124, 12), getFontRenderer))
    searchEdit.setTextColor(-1)
    searchEdit.setDisabledTextColour(-1)
    searchEdit.setMaxStringLength(256)
    searchEdit.setText("")

    val resetBt = widgets.add(new WidgetButtonIcon(Point(152, 4), resetClicked, Textures.Button16.base, Textures.Button16.hover))
    resetBt.icon = Textures.Button16.disabled
    resetBt.hover = Misc.toLocal("pressure.label.configurator.clear")

    for (y <- 0 until 3; x <- 0 until 9)
      widgets.add(new WidgetFluidSelector(Point(8 + x * 18, 22 + y * 18), this, x + y * 9))

    import scala.collection.JavaConversions._

    allFluids = (for ((name, fluid) <- FluidRegistry.getRegisteredFluids)
      yield fluid.getLocalizedName(new FluidStack(fluid, 1)) -> fluid).toVector.sortBy(_._1.toLowerCase(Locale.US))
  }

  def resetClicked(bt: WidgetButtonIcon) {
    NetworkHandler.sendToServer(MsgUnsetFluidFilter())
    Client.player.closeScreen()
  }

  override def updateScreen() = {
    val search = searchEdit.getText.toLowerCase(Locale.US)
    if (search != lastSearch) {
      lastSearch = search
      displayMap = (
        for (((name, fluid), idx) <- allFluids.zipWithIndex
             if search.isEmpty || name.toLowerCase(Locale.US).contains(search)) yield idx)
        .splitAt(27)._1.zipWithIndex.map(_.swap).toMap
    }
    super.updateScreen()
  }

  override def onGuiClosed() {
    Keyboard.enableRepeatEvents(false)
    super.onGuiClosed()
  }
}
