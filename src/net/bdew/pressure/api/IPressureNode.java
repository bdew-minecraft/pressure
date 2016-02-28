/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.api;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Base interface for nodes of the pressure network
 */
public interface IPressureNode {
    /**
     * Return the position of the object in world.
     * Funny name due to reobfuscation issues.
     */
    BlockPos pressureNodePos();

    /**
     * Return the world object.
     * Funny name due to reobfuscation issues.
     */
    World pressureNodeWorld();
}
