package com.mcjty.tut2block.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ProcessorBlock extends Block implements EntityBlock {

    public static final String SCREEN_TUTORIAL_PROCESSOR = "tutorial.screen.processor";
    public static final BooleanProperty BUTTON00 = BooleanProperty.create("button00");
    public static final BooleanProperty BUTTON01 = BooleanProperty.create("button01");
    public static final BooleanProperty BUTTON10 = BooleanProperty.create("button10");
    public static final BooleanProperty BUTTON11 = BooleanProperty.create("button11");

    private static final VoxelShape SHAPE_DOWN = Shapes.box(0, .2, 0, 1, 1, 1);
    private static final VoxelShape SHAPE_UP = Shapes.box(0, 0, 0, 1, .8, 1);
    private static final VoxelShape SHAPE_NORTH = Shapes.box(0, 0, .2, 1, 1, 1);
    private static final VoxelShape SHAPE_SOUTH = Shapes.box(0, 0, 0, 1, 1, .8);
    private static final VoxelShape SHAPE_WEST = Shapes.box(.2, 0, 0, 1, 1, 1);
    private static final VoxelShape SHAPE_EAST = Shapes.box(0, 0, 0, .8, 1, 1);

    public ProcessorBlock() {
        // Let our block behave like a metal block
        super(Properties.of()
                .strength(3.5F)
                .noOcclusion()
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(BlockStateProperties.FACING)) {
            case DOWN -> SHAPE_DOWN;
            case UP -> SHAPE_UP;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ProcessorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            // We don't have anything to do on the client side
            return null;
        } else {
            // Server side we delegate ticking to our block entity
            return (lvl, pos, st, blockEntity) -> {
                if (blockEntity instanceof ProcessorBlockEntity be) {
                    be.tickServer();
                }
            };
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ProcessorBlockEntity) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable(SCREEN_TUTORIAL_PROCESSOR);
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        return new ProcessorContainer(windowId, playerEntity, pos);
                    }
                };
                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, be.getBlockPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(BUTTON00, false)
                .setValue(BUTTON01, false)
                .setValue(BUTTON10, false)
                .setValue(BUTTON11, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BUTTON00, BUTTON01, BUTTON10, BUTTON11);
    }

    public static int getQuadrant(Direction facing, Vec3 hit) {
        // We want to transform this 3D location to 2D so that we can more easily check which quadrant is hit
        double x = getXFromHit(facing, hit);
        double y = getYFromHit(facing, hit);
        // Calculate the correct quadrant
        int quadrant = 0;
        if (x < .5 && y > .5) {
            quadrant = 1;
        } else if (x > .5 && y < .5) {
            quadrant = 2;
        } else if (x > .5 && y > .5) {
            quadrant = 3;
        }
        return quadrant;
    }

    private static double getYFromHit(Direction facing, Vec3 hit) {
        return switch (facing) {
            case UP -> 1 - hit.x;
            case DOWN -> 1 - hit.x;
            case NORTH -> 1 - hit.x;
            case SOUTH -> hit.x;
            case WEST -> hit.z;
            case EAST -> 1 - hit.z;
        };
    }

    private static double getXFromHit(Direction facing, Vec3 hit) {
        return switch (facing) {
            case UP -> hit.z;
            case DOWN -> 1 - hit.z;
            case NORTH -> hit.y;
            case SOUTH -> hit.y;
            case WEST -> hit.y;
            case EAST -> hit.y;
        };
    }
}
