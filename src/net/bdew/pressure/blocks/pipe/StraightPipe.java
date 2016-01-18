/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.pipe;

import net.minecraft.util.IStringSerializable;

public enum StraightPipe implements IStringSerializable {
    X("x"), Y("y"), Z("z"), NONE("none");

    private String name;

    StraightPipe(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
