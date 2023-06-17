package com.mcjty.tut2block.compat;

import com.mcjty.tut2block.Tutorial2Block;
import com.mcjty.tut2block.blocks.ProcessorBlock;
import com.mcjty.tut2block.blocks.ProcessorBlockEntity;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TopCompatibility {

    public static void register() {
        if (!ModList.get().isLoaded("theoneprobe")) {
            return;
        }
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", GetTheOneProbe::new);
    }


    public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {

        public static ITheOneProbe probe;

        @Nullable
        @Override
        public Void apply(ITheOneProbe theOneProbe) {
            probe = theOneProbe;
            Tutorial2Block.LOGGER.info("Enabled support for The One Probe");
            probe.registerProvider(new IProbeInfoProvider() {
                @Override
                public ResourceLocation getID() {
                    return new ResourceLocation("tut2block:default");
                }

                @Override
                public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
                    if (blockState.getBlock() instanceof ProcessorBlock) {
                        Vec3 vec = data.getHitVec().subtract(data.getPos().getX(), data.getPos().getY(), data.getPos().getZ());
                        int quadrant = ProcessorBlock.getQuadrant(data.getSideHit(), vec);

                        ILayoutStyle defaultStyle = probeInfo.defaultLayoutStyle();
                        ILayoutStyle selectedStyle = probeInfo.defaultLayoutStyle().copy().borderColor(Color.rgb(255, 255, 255)).spacing(2);

                        Boolean button0 = blockState.getValue(ProcessorBlock.BUTTON10);
                        probeInfo.horizontal(quadrant == 0 ? selectedStyle : defaultStyle)
                                .text(Component.translatable(ProcessorBlockEntity.ACTION_MELT, button0 ? "On" : "Off"));
                        Boolean button1 = blockState.getValue(ProcessorBlock.BUTTON00);
                        probeInfo.horizontal(quadrant == 1 ? selectedStyle : defaultStyle)
                                .text(Component.translatable(ProcessorBlockEntity.ACTION_BREAK, button1 ? "On" : "Off"));
                        Boolean button2 = blockState.getValue(ProcessorBlock.BUTTON11);
                        probeInfo.horizontal(quadrant == 2 ? selectedStyle : defaultStyle)
                                .text(Component.translatable(ProcessorBlockEntity.ACTION_SOUND, button2 ? "On" : "Off"));
                        Boolean button3 = blockState.getValue(ProcessorBlock.BUTTON01);
                        probeInfo.horizontal(quadrant == 3 ? selectedStyle : defaultStyle)
                                .text(Component.translatable(ProcessorBlockEntity.ACTION_SPAWN, button3 ? "On" : "Off"));
                    }
                }
            });
            return null;
        }
    }
}
