package com.mcjty.tut2block;

import com.mcjty.tut2block.compat.TopCompatibility;
import com.mcjty.tut2block.datagen.DataGeneration;
import com.mcjty.tut2block.network.Channel;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Tutorial2Block.MODID)
public class Tutorial2Block {

    public static final String MODID = "tut2block";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Tutorial2Block() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Registration.init(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(Registration::addCreative);
        modEventBus.addListener(DataGeneration::generate);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Channel.register();
        TopCompatibility.register();
    }
}
