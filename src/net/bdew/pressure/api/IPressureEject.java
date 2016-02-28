/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.api;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

/**
 * Tile entities that implement this interface will be able to receive fluid from the pressure system
 */
public interface IPressureEject extends IPressureNode {
    /**
     * Called when fluid is eject from the pressure network into this TE
     *
     * @param resource Fluid to eject
     * @param face     Side of the block that received the fluid
     * @param doEject  true if fluid should actually be ejected, false if simulation
     * @return amount of fluid accepted
     */
    int eject(FluidStack resource, EnumFacing face, boolean doEject);
}
