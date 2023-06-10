package com.mcjty.tut2block.datagen;

import com.mcjty.tut2block.Registration;
import com.mcjty.tut2block.Tutorial2Block;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.stream.Collectors;

public class TutLootTables extends VanillaBlockLoot {

    @Override
    protected void generate() {
        dropSelf(Registration.SIMPLE_BLOCK.get());
        dropSelf(Registration.COMPLEX_BLOCK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getEntries().stream()
                .filter(e -> e.getKey().location().getNamespace().equals(Tutorial2Block.MODID))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
