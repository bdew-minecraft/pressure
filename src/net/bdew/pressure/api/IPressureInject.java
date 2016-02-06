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

public interface IPressureInject extends IPressureTile {
    /**
     * Forces invalidation of connection info
     *
     * @param side Side from which the connection should be invalidated
     */
    void invalidateConnection(EnumFacing side);
}
