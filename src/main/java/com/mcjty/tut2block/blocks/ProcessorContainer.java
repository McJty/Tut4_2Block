package com.mcjty.tut2block.blocks;

import com.mcjty.tut2block.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class ProcessorContainer extends AbstractContainerMenu {

    private final BlockPos pos;

    public ProcessorContainer(int windowId, Player player, BlockPos pos) {
        super(Registration.PROCESSOR_CONTAINER.get(), windowId);
        this.pos = pos;
        if (player.level().getBlockEntity(pos) instanceof ProcessorBlockEntity processor) {
            processor.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
                addSlot(new SlotItemHandler(h, ProcessorBlockEntity.SLOT_INPUT, 64, 24));
                addSlot(new SlotItemHandler(h, ProcessorBlockEntity.SLOT_OUTPUT+0, 108, 24));
                addSlot(new SlotItemHandler(h, ProcessorBlockEntity.SLOT_OUTPUT+1, 126, 24));
                addSlot(new SlotItemHandler(h, ProcessorBlockEntity.SLOT_OUTPUT+2, 144, 24));
                addSlot(new SlotItemHandler(h, ProcessorBlockEntity.SLOT_OUTPUT+3, 108, 42));
                addSlot(new SlotItemHandler(h, ProcessorBlockEntity.SLOT_OUTPUT+4, 126, 42));
                addSlot(new SlotItemHandler(h, ProcessorBlockEntity.SLOT_OUTPUT+5, 144, 42));
            });
        }
        layoutPlayerInventorySlots(player.getInventory(), 10, 70);
    }

    private int addSlotRange(Container playerInventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Container playerInventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(playerInventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(Container playerInventory, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == ProcessorBlockEntity.SLOT_OUTPUT || index == ProcessorBlockEntity.SLOT_INPUT) {
                if (!this.moveItemStackTo(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (!this.moveItemStackTo(stack, ProcessorBlockEntity.SLOT_INPUT, ProcessorBlockEntity.SLOT_INPUT+1, false)) {
                if (index < 27 + ProcessorBlockEntity.SLOT_COUNT) {
                    if (!this.moveItemStackTo(stack, 27 + ProcessorBlockEntity.SLOT_COUNT, 36 + ProcessorBlockEntity.SLOT_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 36 + ProcessorBlockEntity.SLOT_COUNT && !this.moveItemStackTo(stack, ProcessorBlockEntity.SLOT_COUNT, 27 + ProcessorBlockEntity.SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, Registration.PROCESSOR_BLOCK.get());
    }
}
