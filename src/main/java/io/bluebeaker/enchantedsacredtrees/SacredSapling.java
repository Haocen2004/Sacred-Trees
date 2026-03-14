package io.bluebeaker.enchantedsacredtrees;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SacredSapling extends BushBlock implements BonemealableBlock {

    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    protected BlockState log;
    protected BlockState wood;
    protected BlockState leaves;

    enum Type {
        MEGA, MASSIVE, SACRED_SPRING
    }

    protected Type type;

    public SacredSapling(BlockState log, BlockState wood, BlockState leaves, Properties properties, Type type) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
        this.log = log;
        this.wood = wood;
        this.leaves = leaves;
        this.type = type;
    }

    public SacredSapling(Block log, Block wood, Block leaves, Properties properties, Type type) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
        this.log = log.defaultBlockState();
        this.wood = wood.defaultBlockState();
        this.leaves = leaves.defaultBlockState();
        this.type = type;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return (double) random.nextFloat() < 0.45D;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        this.advanceTree(level, pos, state, random);
    }

    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.cycle(STAGE), 4);
        } else {
            switch (type) {
                case SACRED_SPRING:
                    TreeTypes.generateSacredSpringRubberTree(new MassiveTreeGenerator(log, wood, leaves), level, random, pos);
                    break;
                case MEGA:
                    boolean safe = false;
                    TreeTypes.generateMegaRubberTree(new MassiveTreeGenerator(log, wood, leaves), level, random, pos, safe);
                    break;
                case MASSIVE:
                    TreeTypes.generateMassiveRubberTree(new MassiveTreeGenerator(log, wood, leaves), level, random, pos);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
