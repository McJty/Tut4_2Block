package com.mcjty.tut2block.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;

public class SafeClientTools {

    public static HitResult getClientMouseOver() {
        return Minecraft.getInstance().hitResult;
    }
}
