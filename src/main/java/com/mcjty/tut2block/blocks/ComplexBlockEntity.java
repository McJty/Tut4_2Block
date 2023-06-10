package com.mcjty.tut2block.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.mcjty.tut2block.Registration.TUTORIAL_BLOCK_ENTITY;

public class ComplexBlockEntity extends BlockEntity {

    public ComplexBlockEntity(BlockPos pos, BlockState state) {
        super(TUTORIAL_BLOCK_ENTITY.get(), pos, state);
    }

    public void tickServer() {

    }
}
