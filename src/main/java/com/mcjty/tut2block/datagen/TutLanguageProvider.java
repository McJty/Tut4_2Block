package com.mcjty.tut2block.datagen;

import com.mcjty.tut2block.Registration;
import com.mcjty.tut2block.Tutorial2Block;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class TutLanguageProvider extends LanguageProvider {

    public TutLanguageProvider(PackOutput output, String locale) {
        super(output, Tutorial2Block.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(Registration.SIMPLE_BLOCK.get(), "Simple Block");
        add(Registration.COMPLEX_BLOCK.get(), "Complex Block");
    }
}
