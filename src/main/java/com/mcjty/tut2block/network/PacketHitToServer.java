package com.mcjty.tut2block.network;

import com.mcjty.tut2block.Tutorial2Block;
import com.mcjty.tut2block.blocks.ProcessorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record PacketHitToServer(BlockPos pos, int button) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(Tutorial2Block.MODID, "hit");

    public static PacketHitToServer create(FriendlyByteBuf buf) {
        return new PacketHitToServer(buf.readBlockPos(), buf.readByte());
    }

    public static PacketHitToServer create(BlockPos pos, int button) {
        return new PacketHitToServer(pos, button);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeByte(button);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                if (player.level().getBlockEntity(pos) instanceof ProcessorBlockEntity processor) {
                    processor.hit(player, button);
                }
            });
        });
    }
}
