/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.fmp.traits

import codechicken.lib.data.{MCDataInput, MCDataOutput}
import codechicken.multipart.TMultiPart
import net.bdew.lib.Event
import net.bdew.lib.data.base.{DataSlot, DataSlotContainer, UpdateKind}
import net.minecraft.nbt.NBTTagCompound

trait TDataSlots extends TMultiPart with DataSlotContainer {
  val serverTick = Event()

  override def onServerTick(f: () => Unit) = serverTick.listen(f)
  override def getWorldObject = world

  override def dataSlotChanged(slot: DataSlot) = {
    if (getWorldObject != null) {
      if (slot.updateKind.contains(UpdateKind.GUI))
        lastChange = getWorldObject.getTotalWorldTime
      if (slot.updateKind.contains(UpdateKind.WORLD) && !getWorldObject.isRemote)
        sendDescUpdate()
      if (slot.updateKind.contains(UpdateKind.SAVE))
        tile.markDirty()
    }
  }

  override def writeDesc(packet: MCDataOutput) = {
    val tag = new NBTTagCompound
    doSave(UpdateKind.WORLD, tag)
    packet.writeNBTTagCompound(tag)
  }

  override def readDesc(packet: MCDataInput) = {
    doLoad(UpdateKind.WORLD, packet.readNBTTagCompound())
  }

  override final def update() = {
    // can't call super here as something (ASM voodoo i guess) eats the synthetic needed to do so
    if (world != null && !world.isRemote)
      serverTick.trigger()
  }

  override def save(tag: NBTTagCompound) = doSave(UpdateKind.SAVE, tag)
  override def load(tag: NBTTagCompound) = doLoad(UpdateKind.SAVE, tag)
}
