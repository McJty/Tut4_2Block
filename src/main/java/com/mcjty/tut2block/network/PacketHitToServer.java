package com.mcjty.tut2block.network;

import com.mcjty.tut2block.blocks.ProcessorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketHitToServer {

    private final BlockPos pos;
    private final int button;

    public PacketHitToServer(BlockPos pos, int button) {
        this.pos = pos;
        this.button = button;
    }

    public PacketHitToServer(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        button = buf.readByte();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeByte(button);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are server side
            ServerPlayer player = ctx.getSender();
            if (player.level().getBlockEntity(pos) instanceof ProcessorBlockEntity processor) {
                processor.hit(player, button);
            }
        });
        return true;
    }

}
