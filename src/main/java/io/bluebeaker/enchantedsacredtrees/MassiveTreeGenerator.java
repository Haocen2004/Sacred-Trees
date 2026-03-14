package io.bluebeaker.enchantedsacredtrees;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

import java.util.ArrayList;
import java.util.Random;

public class MassiveTreeGenerator {
	public MassiveTreeGenerator(BlockState log, BlockState wood, BlockState leaves) {
		this.leaves=leaves;
		this.log=log;
		this.wood=wood;
	}
	public MassiveTreeGenerator(BlockState log, BlockState wood, BlockState leaves, BlockState lights, BlockState vines,BlockState vines2) {
		this.leaves=leaves;
		this.log=log;
		this.wood=wood;
		this.lights=lights;
		this.vines=vines;
		this.vines2=vines2;
		this.genLightsAndVines=true;
	}
	// Parametric blockstates to use
	public BlockState leaves;
	public BlockState log;
	public BlockState wood;
	public BlockState lights = null;
	public BlockState vines = null;
	public BlockState vines2 = null;
	public boolean genLightsAndVines = false;
	/**
	 * Contains three sets of two values that provide complimentary indices for a given 'major' index - 1 and 2 for 0, 0
	 * and 2 for 1, and 0 and 1 for 2.
	 */
	private static final byte[] otherCoordPairs = new byte[] { (byte) 2, (byte) 0, (byte) 0, (byte) 1, (byte) 2, (byte) 1 };
	private static final float PI = (float) Math.PI;

	private Random rand = new Random();

	/** Running variables */
	private Level world;
	private int[] basePos = new int[] { 0, 0, 0 };
	private int heightLimit = 0;
	private int minHeight = -1;
	private int height;
	private int leafBases;
	private int density;

	/** Setup variables */
	private float heightAttenuation = 0.45f;
	private float branchSlope = 0.45f;
	private float scaleWidth = 4.0f;
	private float branchDensity = 3.0f;
	private int trunkSize = 11;
	private boolean slopeTrunk = false;
	private boolean safeGrowth = false;

	/**
	 * Sets the limit of the random value used to initialize the height limit.
	 */
	private int heightLimitLimit = 250;

	/**
	 * Sets the distance limit for how far away the generator will populate leaves from the base leaf node.
	 */
	private int leafDistanceLimit = 4;

	private int leafNodesLength;
	/** Contains a list of a points at which to generate groups of leaves. */
	private int[][] leafNodes;


	private void setup() {

		leafBases = Mth.ceil(heightLimit * heightAttenuation);
		density = Math.max(1, (int) (1.382D + Math.pow(branchDensity * heightLimit / 13.0D, 2.0D)));
	}

	private float layerSize(int par1) {

		if (par1 < leafBases)
			return -1.618F;
		else {
			float var2 = heightLimit * .5F;
			float var3 = heightLimit * .5F - par1;
			float var4;

			if (var3 == 0.0F) {
				var4 = var2;
			} else if (Math.abs(var3) >= var2) {
				return 0.0F;
			} else {
				var4 = (float) Math.sqrt(var2 * var2 - var3 * var3);
			}

			var4 *= 0.5F;
			return var4;
		}
	}

	/**
	 * Generates a list of leaf nodes for the tree, to be populated by generateLeaves.
	 */
	private void generateLeafNodeList() {

		int var1 = density;

		int[] basePos = this.basePos;
		int[][] var2 = new int[var1 * heightLimit][4];
		int var3 = basePos[1] + heightLimit - leafDistanceLimit;
		int var4 = 1;
		int var5 = basePos[1] + height;
		int var6 = var3 - basePos[1];
		var2[0][0] = basePos[0];
		var2[0][1] = var3;
		var2[0][2] = basePos[2];
		var2[0][3] = var5;
		--var3;

		while (var6 >= 0) {
			int var7 = 0;
			float var8 = this.layerSize(var6);

			if (var8 > 0.0F)
				for (float var9 = 0.5f; var7 < var1; ++var7) {
					float var11 = scaleWidth * var8 * (rand.nextFloat() + 0.328f);
					float var13 = rand.nextFloat() * 2.0f * PI;
					int var15 = Mth.floor(var11 * Math.sin(var13) + basePos[0] + var9);
					int var16 = Mth.floor(var11 * Math.cos(var13) + basePos[2] + var9);
					int[] var17 = new int[] { var15, var3, var16 };
					int[] var18 = new int[] { var15, var3 + leafDistanceLimit, var16 };

					if (this.checkBlockLine(var17, var18) == -1) {
						int t;
						double var20 = Math.sqrt((t = basePos[0] - var17[0]) * t + (t = basePos[2] - var17[2]) * t);
						int var22 = (int) (var20 * branchSlope);

						int[] var19 = new int[] { basePos[0], Math.min(var17[1] - var22, var5), basePos[2] };

						if (this.checkBlockLine(var19, var17) == -1) {
							var2[var4][0] = var15;
							var2[var4][1] = var3;
							var2[var4][2] = var16;
							var2[var4][3] = var19[1];
							++var4;
						}
					}
				}
			--var3;
			--var6;
		}

		leafNodes = var2;
		leafNodesLength = var4;
	}
	private void genVines(Level world, int x, int y, int z, int length){
		for(int i=1;i<length;i++){
			if(!world.getBlockState(new BlockPos(x,y-i,z)).isAir()) return;
			this.setBlockAndNotifyAdequately(world, x, y-i, z, vines);
		}
		if(vines2!=null && world.getBlockState(new BlockPos(x,y-length,z)).isAir())
		this.setBlockAndNotifyAdequately(world, x, y-length, z, vines2);
	}
	private void genLeafLayer(int x, int y, int z, final int size) {

		int t;
		final int X = x;
		final int Z = z;
		final float maxDistSq = size * size;

		for (int xMod = -size; xMod <= size; ++xMod) {
			x = X + xMod;
			final int xDistSq = xMod * xMod + (((t = xMod >> 31) ^ xMod) - t);

			for (int zMod = 0; zMod <= size;) { // negative values handled below
				final float distSq = xDistSq + zMod * zMod + zMod + 0.5f;

				if (distSq > maxDistSq) {
					break;
				} else {
					t = -1;
					do {
						z = Z + zMod * t;
						BlockPos placementPos = placement.set(x, y, z);
						BlockState state = world.getBlockState(placementPos);
						Block block = state.getBlock();
						BlockState blockToSet = leaves;
						if (this.genLightsAndVines){
							float randFloat = rand.nextFloat();
							if(randFloat<0.03f && world.getBlockState(placementPos.below()).isAir()){
								if(this.lights!=null && randFloat<0.02f) blockToSet=lights;
								else if(this.vines!=null) this.genVines(world, x, y, z, rand.nextInt(this.height/3));
							}
						}
						if (safeGrowth ? canReplaceWithLeaves(state) :
								block != Blocks.BEDROCK) {
									this.setBlockAndNotifyAdequately(world, x, y, z, blockToSet);
								}
						if (t == 1) break;
						t = 1;
					} while (true);
					++zMod;
				}
			}
		}
	}

	/**
	 * Generates the leaf portion of the tree as specified by the leafNodes list.
	 */
	private void generateLeaves() {

		int[][] leafNodes = this.leafNodes;

		for (int i = 0, e = leafNodesLength; i < e; ++i) {
			int[] n = leafNodes[i];
			int x = n[0], yO = n[1], z = n[2];
			int y = 0;

			for (int var5 = y + leafDistanceLimit; y < var5; ++y) {
				int size = (y != 0) && y != leafDistanceLimit - 1 ? 3 : 2;
				genLeafLayer(x, yO++, z, size);
			}
		}
	}

	private int[] placeScratch = new int[3];

	private void placeBlockLine(int[] par1, int[] par2, BlockState logBS, BlockState woodBS) {

		int t;
		int[] var4 = placeScratch;
		byte var6 = 0;

		for (byte i = 0; i < 3; ++i) {
			int a = par2[i] - par1[i], b = ((t = a >> 31) ^ a) - t;
			var4[i] = a;
			if (b > ((a = var4[var6]) ^ (t = a >> 31)) - t)
				var6 = i;
		}

		if (var4[var6] != 0) {
			byte var7 = otherCoordPairs[var6];
			byte var8 = otherCoordPairs[var6 + 3];
			byte var9;

			if (var4[var6] > 0) {
				var9 = 1;
			} else {
				var9 = -1;
			}

			float var10 = (float) var4[var7] / (float) var4[var6];
			float var12 = (float) var4[var8] / (float) var4[var6];
			int var16 = var4[var6] + var9;

			int[] var14 = var4;

			for (int var15 = 0; var15 != var16; var15 += var9) {
				var14[var6] = Mth.floor(par1[var6] + var15 + 0.5F);
				var14[var7] = Mth.floor(par1[var7] + var15 * var10 + 0.5F);
				var14[var8] = Mth.floor(par1[var8] + var15 * var12 + 0.5F);
				BlockState state2 = logBS;
				int var18 = var14[0] - par1[0];
				var18 = ((t = var18 >> 31) ^ var18) - t;
				int var19 = var14[2] - par1[2];
				var19 = ((t = var19 >> 31) ^ var19) - t;
				int var20 = Math.max(var18, var19);

				if (var20 > 0) {
					if (var18 == var20 || var19 == var20) {
						state2 = woodBS;
					}
				}
				this.setBlockAndNotifyAdequately(world, var14[0], var14[1], var14[2], state2);
			}
		}
	}

	/**
	 * Places the trunk for the big tree that is being generated. Able to generate double-sized trunks by changing a
	 * field that is always 1 to 2.
	 */
	private void generateTrunk(BlockPos base) {

		int x = basePos[0];
		int y = basePos[1];
		int maxY = basePos[1] + height;
		int z = basePos[2];

		int[] bottomPoint = new int[] { x, y, z };
		int[] topPoint = new int[] { x, maxY, z };

		double lim = 400f / trunkSize;

		world.getChunk(base).setBlockState(base, Blocks.AIR.defaultBlockState(), false);
		for (int i = -trunkSize; i <= trunkSize; i++) {
			bottomPoint[0] = x + i;
			topPoint[0] = x + i;

			for (int j = -trunkSize; j <= trunkSize; j++) {
				if ((j * j + i * i) * 4 < trunkSize * trunkSize * 5) {
					bottomPoint[2] = z + j;
					topPoint[2] = z + j;

					if (slopeTrunk)
						topPoint[1] = y + sinc2(lim * i, lim * j, height) - (rand.nextInt(3) - 1);

					this.placeBlockLine(bottomPoint, topPoint, log, wood);
					this.setBlockAndNotifyAdequately(world, topPoint[0], topPoint[1], topPoint[2],
							wood);
				}
			}
		}
	}

	private static final int sinc2(final double x, final double z, final int y) {

		final double pi = Math.PI, pi2 = pi / 1.5;
		double r;
		r = Math.sqrt((r = (x / pi)) * r + (r = (z / pi)) * r) * pi / 180;
		if (r == 0) return y;
		return (int) Math.round(y * (((Math.sin(r) / r) + (Math.sin(r * pi2) / (r * pi2))) / 2));
	}

	/**
	 * Generates additional wood blocks to fill out the bases of different leaf nodes that would otherwise degrade.
	 */
	void generateLeafNodeBases() {

		int[] start = new int[] { basePos[0], basePos[1], basePos[2] };
		int[][] leafNodes = this.leafNodes;

		int heightLimit = (int) (this.heightLimit * 0.2f);
		for (int i = 0, e = leafNodesLength; i < e; ++i) {
			int[] end = leafNodes[i];
			start[1] = end[3];
			int height = start[1] - basePos[1];

			if (height >= heightLimit) {
				this.placeBlockLine(start, end,	log, wood);
			}
		}
	}

	private int[] checkScratch = new int[3];

	/**
	 * Checks a line of blocks in the world from the first coordinate to triplet to the second, returning the distance
	 * (in blocks) before a non-air, non-leaf block is encountered and/or the end is encountered.
	 */
	private int checkBlockLine(int[] par1, int[] par2) {

		int t;
		int[] var3 = checkScratch;
		byte var5 = 0;

		for (byte i = 0; i < 3; ++i) {
			int a = par2[i] - par1[i], b = ((t = a >> 31) ^ a) - t;
			var3[i] = a;
			if (b > ((a = var3[var5]) ^ (t = a >> 31)) - t)
				var5 = i;
		}

		if (var3[var5] == 0)
			return -1;
		else {
			byte var6 = otherCoordPairs[var5];
			byte var7 = otherCoordPairs[var5 + 3];
			byte var8;

			if (var3[var5] > 0) {
				var8 = 1;
			} else {
				var8 = -1;
			}

			float var9 = (float) var3[var6] / (float) var3[var5];
			float var11 = (float) var3[var7] / (float) var3[var5];
			int var14 = 0;
			int var15 = var3[var5] + var8;

			int[] var13 = var3;

			for (; var14 != var15; var14 += var8) {
				var13[var5] = par1[var5] + var14;
				var13[var6] = Mth.floor(par1[var6] + var14 * var9);
				var13[var7] = Mth.floor(par1[var7] + var14 * var11);
				BlockPos pos = placement.set(var13[0], var13[1], var13[2]);
				BlockState state = world.getBlockState(pos);
				Block block = state.getBlock();

				if (safeGrowth ? !(canReplaceWithLogs(state) ||
						block instanceof SacredSapling) :
						block == Blocks.BEDROCK)
					break;
			}

			return var14 == var15 ? -1 : ((t = var14 >> 31) ^ var14) - t;
		}
	}

	/**
	 * Returns a boolean indicating whether or not the current location for the tree, spanning basePos to to the height
	 * limit, is valid.
	 */
	private boolean validTreeLocation() {

		int newHeight = Math.min(heightLimit + basePos[1], 255) - basePos[1];
		if (newHeight < minHeight)
			return false;
		heightLimit = newHeight;

		BlockPos saplingPos = new BlockPos(basePos[0], basePos[1], basePos[2]);
		BlockState saplingState = world.getBlockState(saplingPos);
		if (!saplingState.canSurvive(world, saplingPos)) {
			// Fallback: check if block below is dirt-like
			BlockPos belowPos = saplingPos.below();
			BlockState belowState = world.getBlockState(belowPos);
			if (!belowState.is(BlockTags.DIRT) && !belowState.is(BlockTags.NYLIUM)
					&& !belowState.is(Blocks.MYCELIUM) && !belowState.is(Blocks.SOUL_SOIL))
				return false;
		}

		{
			int[] var5 = new int[] { basePos[0], basePos[1], basePos[2] };
			int[] var6 = new int[] { basePos[0], basePos[1] + heightLimit - 1, basePos[2] };

			newHeight = this.checkBlockLine(var5, var6);

			if (newHeight == -1) newHeight = heightLimit;
			if (newHeight < minHeight)
				return false;

			heightLimit = Math.min(newHeight, heightLimitLimit);
			height = (int) (heightLimit * heightAttenuation);
			if (height >= heightLimit)
				height = heightLimit - 1;
			height += rand.nextInt(heightLimit - height);

			if (safeGrowth) {
				int var1 = basePos[0];
				int var2 = basePos[1];
				int var3 = basePos[1] + height;
				int var4 = basePos[2];

				var5 = new int[] { var1, var2, var4 };
				var6 = new int[] { var1, var3, var4 };

				double lim = 400f / trunkSize;

				for (int i = -trunkSize; i <= trunkSize; i++) {
					var5[0] = var1 + i;
					var6[0] = var1 + i;

					for (int j = -trunkSize; j <= trunkSize; j++) {
						if ((j * j + i * i) * 4 < trunkSize * trunkSize * 5) {
							var5[2] = var4 + j;
							var6[2] = var4 + j;

							if (slopeTrunk)
								var6[1] = var2 + sinc2(lim * i, lim * j, height);

							int t = checkBlockLine(var5, var6);
							if (t != -1)
								return false;
						}
					}
				}
			}

			return true;
		}
	}

	public MassiveTreeGenerator setTreeScale(float height, float width, float leaves) {

		heightLimitLimit = (int) (height * 12.0D);
		minHeight = heightLimitLimit / 2;
		trunkSize = (int) Math.round((height / 2D));

		if (minHeight > 30)
			leafDistanceLimit = 5;
		else
			leafDistanceLimit = minHeight / 8;

		scaleWidth = width;
		branchDensity = leaves;
		return this;
	}

	public MassiveTreeGenerator setMinTrunkSize(int radius) {

		trunkSize = Math.max(radius, trunkSize);
		return this;
	}

	public MassiveTreeGenerator setLeafAttenuation(float a) {

		heightAttenuation = a;
		return this;
	}

	public MassiveTreeGenerator setSloped(boolean s) {

		slopeTrunk = s;
		return this;
	}

	public MassiveTreeGenerator setSafe(boolean s) {

		safeGrowth = s;
		return this;
	}

	public boolean generate(Level world, RandomSource par2Random, BlockPos pos) {

		long time = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		this.world = world;
		long var6 = par2Random.nextLong();
		rand.setSeed(var6);
		basePos[0] = pos.getX();
		basePos[1] = pos.getY();
		basePos[2] = pos.getZ();
		if (heightLimit == 0)
			heightLimit = heightLimitLimit;
		if (minHeight == -1)
			minHeight = 80;

		if (!this.validTreeLocation())
			return false;
		else {
			this.setup();
			time = System.currentTimeMillis() - time;
			logDebug("Verified spawn position of massive rubber tree in: " + time + "ms");
			long time2 = time = System.currentTimeMillis();
			this.generateLeafNodeList();
			long nodes = System.currentTimeMillis();
			logDebug("Generated nodes in: " + (nodes-time2) + "ms");
			lastTime = System.currentTimeMillis();
			this.generateLeaves();
			long leaves = System.currentTimeMillis();
			logDebug("Generated leaves in: " + (leaves-nodes) + "ms");
			this.generateLeafNodeBases();
			long bases = System.currentTimeMillis();
			logDebug("Generated bases in: " + (bases-leaves) + "ms");
			this.generateTrunk(pos);
			long trunk = System.currentTimeMillis();
			time = System.currentTimeMillis() - time;
			logDebug("Generated massive rubber tree in: " + time + "ms");
			trunk -= bases; bases -= leaves; leaves -= nodes; nodes -= time2;
			logDebug(String.format("%s for trunk, %s for leaf nodes, %s for leaves, %s for branches", trunk, nodes, leaves, bases));
			logDebug("\tTree contains " + blocksAdded + " Blocks");
			time = System.currentTimeMillis();
			this.updateChunks();
			time = System.currentTimeMillis() - time;
			logDebug("Lit massive rubber tree in: " + time + "ms");
			return true;
		}
	}

	private static boolean canReplaceWithLeaves(BlockState state) {
		return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES);
	}

	private static boolean canReplaceWithLogs(BlockState state) {
		return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS);
	}

	private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("Tree logger");
	private int blocksAdded = 0;
	private long startTime = 0;
	private long lastTime = 0;
	private BlockPos.MutableBlockPos placement = new BlockPos.MutableBlockPos();
	private ArrayList<LevelChunk> chunksToUpdate = new ArrayList<LevelChunk>();

	public void setBlockAndNotifyAdequately(Level world, int x, int y, int z, BlockState state) {
		BlockPos pos = new BlockPos(x,y,z);
		if(safeGrowth && !canReplaceWithLogs(world.getBlockState(pos))) return;
		LevelChunk chunk = world.getChunkAt(pos);
		chunk.setBlockState(pos, state, false);
		((ServerLevel)world).getChunkSource().blockChanged(pos);
		if(!chunksToUpdate.contains(chunk)) chunksToUpdate.add(chunk);
		++blocksAdded;
		if(blocksAdded%1000000==0){
			long timeMillis = System.currentTimeMillis();
			logDebug("Added 1M Blocks in "+(timeMillis-lastTime)+"ms, "+blocksAdded/1000000+"M blocks/"+ (timeMillis-startTime)+"ms total");
			lastTime=timeMillis;
		}
	}
	public void updateChunks(){
		LevelLightEngine lightEngine = world.getLightEngine();
		for (LevelChunk chunk: chunksToUpdate){
			chunk.setLightCorrect(false);
			for (int i=0;i<chunk.getSections().length;i++){
				lightEngine.updateSectionStatus(SectionPos.of(chunk.getPos(),i), true);
			}
		}
	}
	public static boolean debug = true;
	private void logDebug(String message){
		if (debug)logger.info(message);
	}
}
