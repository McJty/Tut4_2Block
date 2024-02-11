package com.mcjty.tut2block;

import com.mcjty.tut2block.compat.TopCompatibility;
import com.mcjty.tut2block.datagen.DataGeneration;
import com.mcjty.tut2block.network.Channel;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Tutorial2Block.MODID)
public class Tutorial2Block {

    public static final String MODID = "tut2block";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Tutorial2Block(IEventBus modEventBus) {
        Registration.init(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(Registration::addCreative);
        modEventBus.addListener(DataGeneration::generate);
        modEventBus.addListener(Channel::onRegisterPayloadHandler);
        modEventBus.addListener(this::registerCapabilities);

        NeoForge.EVENT_BUS.register(new EventHandlers());
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.COMPLEX_BLOCK_ENTITY.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Registration.PROCESSOR_BLOCK_ENTITY.get(), (o, direction) -> {
            if (direction == null) {
                return o.getItemHandler().get();
            }
            if (direction == Direction.DOWN) {
                return o.getOutputItemHandler().get();
            }
            return o.getInputItemHandler().get();
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        TopCompatibility.register();
    }
}
