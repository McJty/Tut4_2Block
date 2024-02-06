package com.mcjty.tut2block.datagen;

import com.mcjty.tut2block.Registration;
import com.mcjty.tut2block.Tutorial2Block;
import com.mcjty.tut2block.blocks.ProcessorBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public class TutBlockStates extends BlockStateProvider {

    public TutBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Tutorial2Block.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(Registration.SIMPLE_BLOCK.get());
        simpleBlock(Registration.COMPLEX_BLOCK.get());
        registerProcessor();
    }

    private void registerProcessor() {
        Supplier<ProcessorBlock> processor = Registration.PROCESSOR_BLOCK;
        String path = "processor";

        BlockModelBuilder base = models().getBuilder("block/" + path + "_main");
        base.parent(models().getExistingFile(mcLoc("cube")));

        base.element()
                .from(0f, 0f, 0f)
                .to(16f, 14f, 16f)
                .allFaces((direction, faceBuilder) -> faceBuilder.texture("#txt"))
                .end();

        base.texture("txt", modLoc("block/processor_main"));
        base.texture("particle", modLoc("block/processor_main"));

        base.renderType("solid");

        createProcessorModel(processor.get(), path, base);
    }

    private void createProcessorModel(Block block, String path, BlockModelBuilder frame) {
        BlockModelBuilder singleOff00 = buttonBuilderOff(path, "singleoff00", 2, 14, 2);
        BlockModelBuilder singleOn00 = buttonBuilderOn(path, "singleon00", 2, 14, 2);
        BlockModelBuilder singleOff10 = buttonBuilderOff(path, "singleoff10", 10, 14, 2);
        BlockModelBuilder singleOn10 = buttonBuilderOn(path, "singleon10", 10, 14, 2);
        BlockModelBuilder singleOff01 = buttonBuilderOff(path, "singleoff01", 2, 14, 10);
        BlockModelBuilder singleOn01 = buttonBuilderOn(path, "singleon01", 2, 14, 10);
        BlockModelBuilder singleOff11 = buttonBuilderOff(path, "singleoff11", 10, 14, 10);
        BlockModelBuilder singleOn11 = buttonBuilderOn(path, "singleon11", 10, 14, 10);

        MultiPartBlockStateBuilder bld = getMultipartBuilder(block);

        for (Direction dir : Direction.values()) {
            int angleOffset = dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360;
            int rotationX = dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0;
            bld.part().modelFile(frame).rotationX(rotationX).rotationY(angleOffset).addModel().condition(BlockStateProperties.FACING, dir).end();
            bld.part().modelFile(singleOff00).rotationX(rotationX).rotationY(angleOffset).addModel().condition(BlockStateProperties.FACING, dir).condition(ProcessorBlock.BUTTON00, false);
            bld.part().modelFile(singleOn00).rotationX(rotationX).rotationY(angleOffset).addModel().condition(BlockStateProperties.FACING, dir).condition(ProcessorBlock.BUTTON00, true);
            bld.part().modelFile(singleOff10).rotationX(rotationX).rotationY(angleOffset).addModel().condition(BlockStateProperties.FACING, dir).condition(ProcessorBlock.BUTTON10, false);
            bld.part().modelFile(singleOn10).rotationX(rotationX).rotationY(angleOffset).addModel().condition(BlockStateProperties.FACING, dir).condition(ProcessorBlock.BUTTON10, true);
            bld.part().modelFile(singleOff01).rotationX(rotationX).rotationY(angleOffset).addModel().condition(BlockStateProperties.FACING, dir).condition(ProcessorBlock.BUTTON01, false);
            bld.part().modelFile(singleOn01).rotationX(rotationX).rotationY(angleOffset).addModel().condition(BlockStateProperties.FACING, dir).condition(ProcessorBlock.BUTTON01, true);
            bld.part().modelFile(singleOff11).rotationX(rotationX).rotationY(angleOffset).addModel().condition(BlockStateProperties.FACING, dir).condition(ProcessorBlock.BUTTON11, false);
            bld.part().modelFile(singleOn11).rotationX(rotationX).rotationY(angleOffset).addModel().condition(BlockStateProperties.FACING, dir).condition(ProcessorBlock.BUTTON11, true);
        }
    }

    private BlockModelBuilder buttonBuilderOn(String path, String name, int x, int y, int z) {
        return models().getBuilder("block/" + path + "/" + name)
                .element()
                .from(x, y, z)
                .to(x+4, y+2, z+4)
                .allFaces((direction, faceBuilder) -> faceBuilder.texture("#button"))
                .end()
                .texture("button", modLoc("block/processor_on"));
    }

    private BlockModelBuilder buttonBuilderOff(String path, String name, int x, int y, int z) {
        return models().getBuilder("block/" + path + "/" + name)
                .element()
                .from(x, y, z)
                .to(x+4, y+.5f, z+4)
                .allFaces((direction, faceBuilder) -> faceBuilder.texture("#button"))
                .end()
                .texture("button", modLoc("block/processor_off"));
    }
}

