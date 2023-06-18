package com.mcjty.tut2block.blocks;

import com.mcjty.tut2block.Registration;
import com.mcjty.tut2block.tools.AdaptedItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ProcessorBlockEntity extends BlockEntity {

    public static final String ITEMS_INPUT_TAG = "Input";
    public static final String ITEMS_OUTPUT_TAG = "Output";

    public static final String ACTION_MELT = "tutorial.message.melt";
    public static final String ACTION_BREAK = "tutorial.message.break";
    public static final String ACTION_SOUND = "tutorial.message.sound";
    public static final String ACTION_SPAWN = "tutorial.message.spawn";

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_INPUT_COUNT = 1;

    public static final int SLOT_OUTPUT = 0;
    public static final int SLOT_OUTPUT_COUNT = 6;

    public static final int SLOT_COUNT = SLOT_INPUT_COUNT + SLOT_OUTPUT_COUNT;

    private final ItemStackHandler inputItems = createItemHandler(SLOT_INPUT_COUNT);
    private final ItemStackHandler outputItems = createItemHandler(SLOT_OUTPUT_COUNT);
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new CombinedInvWrapper(inputItems, outputItems));
    private final LazyOptional<IItemHandler> inputItemHandler = LazyOptional.of(() -> new AdaptedItemHandler(inputItems) {
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    });
    private final LazyOptional<IItemHandler> outputItemHandler = LazyOptional.of(() -> new AdaptedItemHandler(outputItems) {
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
    });

    public ProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.PROCESSOR_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
        inputItemHandler.invalidate();
        outputItemHandler.invalidate();
    }

    public void tickServer() {
        if (level.getGameTime() % 10 != 0) {
            return;
        }

        // Depending on the pressed buttons we will do some processing for the current item
        boolean button0 = getBlockState().getValue(ProcessorBlock.BUTTON10);
        boolean button1 = getBlockState().getValue(ProcessorBlock.BUTTON00);
        boolean button2 = getBlockState().getValue(ProcessorBlock.BUTTON11);
        boolean button3 = getBlockState().getValue(ProcessorBlock.BUTTON01);

        if (button0 || button1 || button2  || button3) {
            ItemStack stack = inputItems.extractItem(SLOT_INPUT, 1, false);
            if (!stack.isEmpty()) {
                // We have an item in the input slot. We will do some processing depending on
                // the pressed buttons and put the result in one of the output slots
                if (button0) {
                    insertOrEject(meltItem(stack));
                }
                if (button1) {
                    insertOrEject(breakAsBlock(stack));
                }
                if (button2) {
                    insertOrEject(playSound(stack));
                }
                if (button3) {
                    insertOrEject(spawnMob(stack));
                }
            }
        }
    }

    // Try to insert the item in the output. Eject if no room
    private void insertOrEject(ItemStack stack) {
        ItemStack itemStack = ItemHandlerHelper.insertItem(outputItems, stack, false);
        if (!itemStack.isEmpty()) {
            ItemEntity entityitem = new ItemEntity(level, worldPosition.getX()+.5, worldPosition.getY() + 1, worldPosition.getZ()+.5, itemStack);
            entityitem.setPickUpDelay(40);
            entityitem.setDeltaMovement(entityitem.getDeltaMovement().multiply(0, 1, 0));
            level.addFreshEntity(entityitem);
        }
    }

    private ItemStack meltItem(ItemStack stack) {
        SimpleContainer container = new SimpleContainer(stack);
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, this.level)
                .map(recipe -> recipe.assemble(container, level.registryAccess())).orElse(ItemStack.EMPTY);
    }

    private ItemStack breakAsBlock(ItemStack stack) {
        // Check if the item is a block item then get the loot that we would get when it is broken
        if (stack.getItem() instanceof BlockItem blockItem) {
            LootParams.Builder paramsBuilder = new LootParams.Builder((ServerLevel) level)
                    .withParameter(LootContextParams.ORIGIN, new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()))
                    .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE));
            List<ItemStack> drops = blockItem.getBlock().getDrops(blockItem.getBlock().defaultBlockState(), paramsBuilder);
            // Return a random item from the drops
            if (drops.isEmpty()) {
                return ItemStack.EMPTY;
            }
            return drops.get(level.random.nextInt(drops.size()));
        } else {
            return stack;
        }
    }

    private ItemStack playSound(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            SoundEvent sound = block.defaultBlockState().getSoundType().getBreakSound();
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1, 1);
        }
        return stack;
    }

    private ItemStack spawnMob(ItemStack stack) {
        if (stack.getItem() instanceof SpawnEggItem spawnEggItem) {
            EntityType<?> type = spawnEggItem.getType(stack.getTag());
            type.spawn((ServerLevel)level, stack, null, worldPosition.above(), MobSpawnType.SPAWN_EGG, false, false);
        }
        return ItemStack.EMPTY;
    }

    public void hit(Player player, int button) {
        // Get the button property from the block state based on button parameter
        BooleanProperty property = switch (button) {
            case 0 -> ProcessorBlock.BUTTON10;
            case 1 -> ProcessorBlock.BUTTON00;
            case 2 -> ProcessorBlock.BUTTON11;
            case 3 -> ProcessorBlock.BUTTON01;
            default -> throw new IllegalStateException("Unexpected value: " + button);
        };
        // Get the right button message
        String message = switch (button) {
            case 0 -> ACTION_MELT;
            case 1 -> ACTION_BREAK;
            case 2 -> ACTION_SOUND;
            case 3 -> ACTION_SPAWN;
            default -> throw new IllegalStateException("Unexpected value: " + button);
        };
        // Toggle the value of this property in the blockstate
        BlockState newState = getBlockState().cycle(property);
        level.setBlockAndUpdate(worldPosition, newState);
        Boolean value = newState.getValue(property);

        // Send a message to the client with the new button state
        player.displayClientMessage(Component.translatable(message, value ? "On" : "Off"), true);

        // Play a button sound
        level.playSound(null, worldPosition, value ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 1, 1);
    }

    public ItemStackHandler getInputItems() {
        return inputItems;
    }

    public ItemStackHandler getOutputItems() {
        return outputItems;
    }

    @Nonnull
    private ItemStackHandler createItemHandler(int slots) {
        return new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(ITEMS_INPUT_TAG, inputItems.serializeNBT());
        tag.put(ITEMS_OUTPUT_TAG, outputItems.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(ITEMS_INPUT_TAG)) {
            inputItems.deserializeNBT(tag.getCompound(ITEMS_INPUT_TAG));
        }
        if (tag.contains(ITEMS_OUTPUT_TAG)) {
            outputItems.deserializeNBT(tag.getCompound(ITEMS_OUTPUT_TAG));
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return itemHandler.cast();
            } else if (side == Direction.DOWN) {
                return outputItemHandler.cast();
            } else {
                return inputItemHandler.cast();
            }
        } else {
            return super.getCapability(cap, side);
        }
    }
}
