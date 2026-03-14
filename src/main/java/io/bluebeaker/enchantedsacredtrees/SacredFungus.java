package io.bluebeaker.enchantedsacredtrees;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SacredFungus extends SacredSapling {

    public BlockState lights;
    public BlockState vines;
    public BlockState vines2;

    public SacredFungus(Block log, Block wood, Block leaves, Block lights, Block vines, Block vines2, Properties properties, Type type) {
        super(log, wood, leaves, properties, type);
        if (lights != null) this.lights = lights.defaultBlockState();
        if (vines != null) this.vines = vines.defaultBlockState();
        if (vines != null && vines2 != null) this.vines2 = vines2.defaultBlockState();
    }

    public SacredFungus(BlockState log, BlockState wood, BlockState leaves, BlockState lights, BlockState vines, BlockState vines2, Properties properties, Type type) {
        super(log, wood, leaves, properties, type);
        this.lights = lights;
        this.vines = vines;
        this.vines2 = vines2;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.is(BlockTags.NYLIUM) || state.is(Blocks.MYCELIUM) || state.is(Blocks.SOUL_SOIL) || super.mayPlaceOn(state, reader, pos);
    }

    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.cycle(STAGE), 4);
        } else {
            switch (type) {
                case SACRED_SPRING:
                    TreeTypes.generateSacredSpringRubberTree(new MassiveTreeGenerator(log, wood, leaves, lights, vines, vines2), level, random, pos);
                    break;
                case MEGA:
                    boolean safe = false;
                    TreeTypes.generateMegaRubberTree(new MassiveTreeGenerator(log, wood, leaves, lights, vines, vines2), level, random, pos, safe);
                    break;
                case MASSIVE:
                    TreeTypes.generateMassiveRubberTree(new MassiveTreeGenerator(log, wood, leaves, lights, vines, vines2), level, random, pos);
                    break;
                default:
                    break;
            }
        }
    }
}
