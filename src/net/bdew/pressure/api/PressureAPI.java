/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.api;

import net.bdew.pressure.api.properties.IFilterable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class PressureAPI {
    public static IPressureHelper HELPER;

    @CapabilityInject(IFilterable.class)
    public static Capability<IFilterable> FILTERABLE;
}
