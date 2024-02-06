package com.mcjty.tut2block.datagen;

import com.mcjty.tut2block.Registration;
import com.mcjty.tut2block.Tutorial2Block;
import com.mcjty.tut2block.blocks.ProcessorBlock;
import com.mcjty.tut2block.blocks.ProcessorBlockEntity;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class TutLanguageProvider extends LanguageProvider {

    public TutLanguageProvider(PackOutput output, String locale) {
        super(output, Tutorial2Block.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(Registration.SIMPLE_BLOCK.get(), "Simple Block");
        add(Registration.COMPLEX_BLOCK.get(), "Complex Block");
        add(Registration.PROCESSOR_BLOCK.get(), "Processor");
        add(ProcessorBlock.SCREEN_TUTORIAL_PROCESSOR, "Processor");
        add(ProcessorBlockEntity.ACTION_MELT, "Melt input: %s");
        add(ProcessorBlockEntity.ACTION_BREAK, "Block loot: %s");
        add(ProcessorBlockEntity.ACTION_SOUND, "Play break sound: %s");
        add(ProcessorBlockEntity.ACTION_SPAWN, "Spawn egg: %s");
    }
}
