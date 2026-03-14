package io.bluebeaker.enchantedsacredtrees;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SaplingBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SacredTreesMod.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SacredTreesMod.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SacredTreesMod.MODID);

    private static final List<Supplier<? extends BlockItem>> ALL_ITEMS = new ArrayList<>();

    public static final SaplingVariant OAK = registerSaplingTypes("oak_sapling", () -> Blocks.OAK_LOG, () -> Blocks.OAK_WOOD, () -> Blocks.OAK_LEAVES);
    public static final SaplingVariant BIRCH = registerSaplingTypes("birch_sapling", () -> Blocks.BIRCH_LOG, () -> Blocks.BIRCH_WOOD, () -> Blocks.BIRCH_LEAVES);
    public static final SaplingVariant SPRUCE = registerSaplingTypes("spruce_sapling", () -> Blocks.SPRUCE_LOG, () -> Blocks.SPRUCE_WOOD, () -> Blocks.SPRUCE_LEAVES);
    public static final SaplingVariant JUNGLE = registerSaplingTypes("jungle_sapling", () -> Blocks.JUNGLE_LOG, () -> Blocks.JUNGLE_WOOD, () -> Blocks.JUNGLE_LEAVES);
    public static final SaplingVariant ACACIA = registerSaplingTypes("acacia_sapling", () -> Blocks.ACACIA_LOG, () -> Blocks.ACACIA_WOOD, () -> Blocks.ACACIA_LEAVES);
    public static final SaplingVariant DARK_OAK = registerSaplingTypes("dark_oak_sapling", () -> Blocks.DARK_OAK_LOG, () -> Blocks.DARK_OAK_WOOD, () -> Blocks.DARK_OAK_LEAVES);
    public static final SaplingVariant CRIMSON = registerFungusTypes("crimson_fungus", () -> Blocks.CRIMSON_STEM, () -> Blocks.CRIMSON_HYPHAE, () -> Blocks.NETHER_WART_BLOCK, () -> Blocks.SHROOMLIGHT, () -> Blocks.WEEPING_VINES_PLANT, () -> Blocks.WEEPING_VINES);
    public static final SaplingVariant WARPED = registerFungusTypes("warped_fungus", () -> Blocks.WARPED_STEM, () -> Blocks.WARPED_HYPHAE, () -> Blocks.WARPED_WART_BLOCK, () -> Blocks.SHROOMLIGHT, null, null);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SACRED_TREES_TAB = CREATIVE_TABS.register("sacred_trees",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.sacred_trees"))
                    .icon(() -> new ItemStack(OAK.MASSIVE.get()))
                    .displayItems((params, output) -> ALL_ITEMS.forEach(s -> output.accept(s.get())))
                    .build());

    private static BlockBehaviour.Properties saplingProps() {
        return BlockBehaviour.Properties.of()
                .noCollission().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY);
    }

    private static SaplingVariant registerSaplingTypes(String basename, Supplier<Block> log, Supplier<Block> wood, Supplier<Block> leaves) {
        DeferredBlock<Block> sacred = BLOCKS.register("sacred_" + basename,
                () -> new SacredSapling(log.get(), wood.get(), leaves.get(), saplingProps(), SacredSapling.Type.SACRED_SPRING));
        DeferredBlock<Block> mega = BLOCKS.register("mega_" + basename,
                () -> new SacredSapling(log.get(), wood.get(), leaves.get(), saplingProps(), SacredSapling.Type.MEGA));
        DeferredBlock<Block> massive = BLOCKS.register("massive_" + basename,
                () -> new SacredSapling(log.get(), wood.get(), leaves.get(), saplingProps(), SacredSapling.Type.MASSIVE));

        DeferredItem<BlockItem> sacredItem = registerBlockItem("sacred_" + basename, sacred, false, Rarity.RARE);
        DeferredItem<BlockItem> megaItem = registerBlockItem("mega_" + basename, mega, false, Rarity.UNCOMMON);
        DeferredItem<BlockItem> massiveItem = registerBlockItem("massive_" + basename, massive, true, Rarity.EPIC);

        return new SaplingVariant(sacred, mega, massive, sacredItem, megaItem, massiveItem);
    }

    private static SaplingVariant registerFungusTypes(String basename, Supplier<Block> log, Supplier<Block> wood, Supplier<Block> leaves,
                                                      Supplier<Block> lights, Supplier<Block> vines, Supplier<Block> vines2) {
        DeferredBlock<Block> sacred = BLOCKS.register("sacred_" + basename,
                () -> new SacredFungus(log.get(), wood.get(), leaves.get(),
                        lights != null ? lights.get() : null, vines != null ? vines.get() : null, vines2 != null ? vines2.get() : null,
                        saplingProps(), SacredSapling.Type.SACRED_SPRING));
        DeferredBlock<Block> mega = BLOCKS.register("mega_" + basename,
                () -> new SacredFungus(log.get(), wood.get(), leaves.get(),
                        lights != null ? lights.get() : null, vines != null ? vines.get() : null, vines2 != null ? vines2.get() : null,
                        saplingProps(), SacredSapling.Type.MEGA));
        DeferredBlock<Block> massive = BLOCKS.register("massive_" + basename,
                () -> new SacredFungus(log.get(), wood.get(), leaves.get(),
                        lights != null ? lights.get() : null, vines != null ? vines.get() : null, vines2 != null ? vines2.get() : null,
                        saplingProps(), SacredSapling.Type.MASSIVE));

        DeferredItem<BlockItem> sacredItem = registerBlockItem("sacred_" + basename, sacred, false, Rarity.RARE);
        DeferredItem<BlockItem> megaItem = registerBlockItem("mega_" + basename, mega, false, Rarity.UNCOMMON);
        DeferredItem<BlockItem> massiveItem = registerBlockItem("massive_" + basename, massive, true, Rarity.EPIC);

        return new SaplingVariant(sacred, mega, massive, sacredItem, megaItem, massiveItem);
    }

    private static DeferredItem<BlockItem> registerBlockItem(String name, DeferredBlock<Block> block, boolean foiled, Rarity rarity) {
        DeferredItem<BlockItem> item;
        if (foiled) {
            item = ITEMS.register(name, () -> new FoiledBlockItem(block.get(), new Item.Properties().rarity(rarity)));
        } else {
            item = ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().rarity(rarity)));
        }
        ALL_ITEMS.add(item);
        return item;
    }

    public static class SaplingVariant {
        public final DeferredBlock<Block> SACRED;
        public final DeferredBlock<Block> MEGA;
        public final DeferredBlock<Block> MASSIVE;
        public final DeferredItem<BlockItem> SACRED_ITEM;
        public final DeferredItem<BlockItem> MEGA_ITEM;
        public final DeferredItem<BlockItem> MASSIVE_ITEM;

        public SaplingVariant(DeferredBlock<Block> sacred, DeferredBlock<Block> mega, DeferredBlock<Block> massive,
                              DeferredItem<BlockItem> sacredItem, DeferredItem<BlockItem> megaItem, DeferredItem<BlockItem> massiveItem) {
            SACRED = sacred;
            MEGA = mega;
            MASSIVE = massive;
            SACRED_ITEM = sacredItem;
            MEGA_ITEM = megaItem;
            MASSIVE_ITEM = massiveItem;
        }
    }
}
