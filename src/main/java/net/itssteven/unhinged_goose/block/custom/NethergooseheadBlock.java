package net.itssteven.unhinged_goose.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class NethergooseheadBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<NethergooseheadBlock> CODEC = simpleCodec(NethergooseheadBlock::new);


    public NethergooseheadBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape NORTH_SHAPE =
            Block.box(0, 0, 13, 16, 16, 16);

    private static final VoxelShape SOUTH_SHAPE =
            Block.box(0, 0, 0, 16, 16, 3);

    private static final VoxelShape EAST_SHAPE =
            Block.box(0, 0, 0, 3, 16, 16);

    private static final VoxelShape WEST_SHAPE =
            Block.box(13, 0, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level,
                               BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST  -> EAST_SHAPE;
            case WEST  -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction face = ctx.getClickedFace();

        if (face.getAxis().isHorizontal()) {
            BlockState state = this.defaultBlockState().setValue(FACING, face);
            if (state.canSurvive(ctx.getLevel(), ctx.getClickedPos())) {
                return state;
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        return level.getBlockState(supportPos)
                .isFaceSturdy(level, supportPos, facing);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {

        if (dir == state.getValue(FACING).getOpposite()
                && !this.canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
