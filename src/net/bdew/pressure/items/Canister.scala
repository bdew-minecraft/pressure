package net.bdew.pressure.items

import java.util

import net.bdew.lib.Misc
import net.bdew.lib.items.SimpleItem
import net.bdew.pressure.config.Tuning
import net.bdew.pressure.{CreativeTabCanisters, CreativeTabPressure}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{FluidRegistry, FluidStack, IFluidContainerItem, IFluidHandler}

object Canister extends SimpleItem("Canister") with IFluidContainerItem {
  lazy val cfg = Tuning.getSection("Items").getSection(name)
  lazy val maxPour = cfg.getInt("MaxPour")
  lazy val capacity = cfg.getInt("Capacity")

  setCreativeTab(CreativeTabPressure)
  setMaxStackSize(1)

  override def getCreativeTabs = Array(CreativeTabPressure, CreativeTabCanisters)
  override def getSubItems(item: Item, tab: CreativeTabs, jstacks: util.List[_]) {
    import scala.collection.JavaConversions._
    val stacks = jstacks.asInstanceOf[java.util.List[ItemStack]]
    if (tab == CreativeTabPressure)
      stacks.add(new ItemStack(this))
    else if (tab == CreativeTabCanisters || tab == null)
      stacks.addAll(
        FluidRegistry.getRegisteredFluids map { case (id, fluid) =>
          val tag = new NBTTagCompound
          val fstack = new FluidStack(fluid, capacity)
          fstack.writeToNBT(tag)
          val item = new ItemStack(this)
          item.setTagCompound(tag)
          item
        }
      )
  }

  def getFluid(stack: ItemStack): FluidStack = FluidStack.loadFluidStackFromNBT(stack.getTagCompound)

  def drain(stack: ItemStack, max: Int, doDrain: Boolean): FluidStack = {
    val fl = getFluid(stack)
    if (fl == null) return null
    val ns = new FluidStack(fl.fluidID, Misc.clamp(fl.amount, 0, max))
    if (doDrain) {
      fl.amount -= ns.amount
      val nbt = new NBTTagCompound()
      if (fl.amount > 0)
        fl.writeToNBT(nbt)
      stack.setTagCompound(nbt)
    }
    return ns
  }

  def fill(stack: ItemStack, fl: FluidStack, doFill: Boolean): Int = {
    val currStack = getFluid(stack)
    if (fl == null || (currStack != null && !currStack.isFluidEqual(fl))) return 0
    val current = if (currStack == null) 0 else currStack.amount
    val toFill = Misc.clamp(fl.amount, 0, capacity - current)
    if (doFill) {
      val newStack = new FluidStack(fl.getFluid, toFill + current)
      val nbt = new NBTTagCompound()
      newStack.writeToNBT(nbt)
      stack.setTagCompound(nbt)
    }
    return toFill
  }

  def getCapacity(container: ItemStack): Int = capacity

  override def addInformation(stack: ItemStack, player: EntityPlayer, lst: util.List[_], par4: Boolean) {
    import scala.collection.JavaConverters._
    val l = lst.asInstanceOf[util.List[String]].asScala
    val fl = getFluid(stack)
    if (fl == null)
      l += Misc.toLocal("deepcore.label.empty")
    else
      l += "%d/%d %s".format(fl.amount, capacity, fl.getFluid.getLocalizedName(fl))
  }

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (world.isRemote) return true
    val te = world.getTileEntity(x, y, z)
    if (te != null && te.isInstanceOf[IFluidHandler]) {
      val fh = te.asInstanceOf[IFluidHandler]
      val fl = drain(stack, maxPour, false)
      if (fl == null) return false
      val dir = ForgeDirection.values()(side)
      val tofill = fh.fill(dir, fl, false)
      if (tofill > 0) {
        fh.fill(dir, drain(stack, tofill, true), true)
        player.swingItem()
        return true
      }
    }
    return false
  }
}
