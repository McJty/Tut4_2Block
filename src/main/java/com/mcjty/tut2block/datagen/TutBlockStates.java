package com.mcjty.tut2block.datagen;

import com.mcjty.tut2block.Registration;
import com.mcjty.tut2block.Tutorial2Block;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TutBlockStates extends BlockStateProvider {

    public TutBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Tutorial2Block.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(Registration.SIMPLE_BLOCK.get());
        simpleBlock(Registration.COMPLEX_BLOCK.get());
    }
}
