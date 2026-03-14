package io.bluebeaker.enchantedsacredtrees;

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
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SacredTreesMod.MODID);

    public static SaplingVariant OAK;
    public static SaplingVariant BIRCH;
    public static SaplingVariant SPRUCE;
    public static SaplingVariant JUNGLE;
    public static SaplingVariant ACACIA;
    public static SaplingVariant DARK_OAK;
    public static SaplingVariant CRIMSON;
    public static SaplingVariant WARPED;

    static {
        OAK = registerSaplingTypes("oak_sapling", Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.OAK_LEAVES);
        BIRCH = registerSaplingTypes("birch_sapling", Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.BIRCH_LEAVES);
        SPRUCE = registerSaplingTypes("spruce_sapling", Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LEAVES);
        JUNGLE = registerSaplingTypes("jungle_sapling", Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LEAVES);
        ACACIA = registerSaplingTypes("acacia_sapling", Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.ACACIA_LEAVES);
        DARK_OAK = registerSaplingTypes("dark_oak_sapling", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LEAVES);
        CRIMSON = registerFungusTypes("crimson_fungus", Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE, Blocks.NETHER_WART_BLOCK, Blocks.SHROOMLIGHT, Blocks.WEEPING_VINES_PLANT, Blocks.WEEPING_VINES);
        WARPED = registerFungusTypes("warped_fungus", Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE, Blocks.WARPED_WART_BLOCK, Blocks.SHROOMLIGHT, null, null);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SACRED_TREES_TAB = CREATIVE_MODE_TABS.register("sacred_trees", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.sacred_trees"))
            .icon(() -> new ItemStack(OAK.MASSIVE_ITEM.get()))
            .displayItems((parameters, output) -> {
                ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            })
            .build());

    private static BlockBehaviour.Properties saplingProperties() {
        return BlockBehaviour.Properties.of()
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .pushReaction(PushReaction.DESTROY);
    }

    private static SaplingVariant registerSaplingTypes(String basename, Block log, Block wood, Block leaves) {
        DeferredBlock<Block> sacred = BLOCKS.register("sacred_" + basename,
                () -> new SacredSapling(log, wood, leaves, saplingProperties(), SacredSapling.Type.SACRED_SPRING));
        DeferredBlock<Block> mega = BLOCKS.register("mega_" + basename,
                () -> new SacredSapling(log, wood, leaves, saplingProperties(), SacredSapling.Type.MEGA));
        DeferredBlock<Block> massive = BLOCKS.register("massive_" + basename,
                () -> new SacredSapling(log, wood, leaves, saplingProperties(), SacredSapling.Type.MASSIVE));

        DeferredItem<BlockItem> sacredItem = ITEMS.register("sacred_" + basename,
                () -> new BlockItem(sacred.get(), new Item.Properties().rarity(Rarity.RARE)));
        DeferredItem<BlockItem> megaItem = ITEMS.register("mega_" + basename,
                () -> new BlockItem(mega.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
        DeferredItem<BlockItem> massiveItem = ITEMS.register("massive_" + basename,
                () -> new FoiledBlockItem(massive.get(), new Item.Properties().rarity(Rarity.EPIC)));

        return new SaplingVariant(sacred, mega, massive, sacredItem, megaItem, massiveItem);
    }

    private static SaplingVariant registerFungusTypes(String basename, Block log, Block wood, Block leaves, Block lights, Block vines, Block vines2) {
        DeferredBlock<Block> sacred = BLOCKS.register("sacred_" + basename,
                () -> new SacredFungus(log, wood, leaves, lights, vines, vines2,
                        saplingProperties().randomTicks(), SacredSapling.Type.SACRED_SPRING));
        DeferredBlock<Block> mega = BLOCKS.register("mega_" + basename,
                () -> new SacredFungus(log, wood, leaves, lights, vines, vines2,
                        saplingProperties().randomTicks(), SacredSapling.Type.MEGA));
        DeferredBlock<Block> massive = BLOCKS.register("massive_" + basename,
                () -> new SacredFungus(log, wood, leaves, lights, vines, vines2,
                        saplingProperties().randomTicks(), SacredSapling.Type.MASSIVE));

        DeferredItem<BlockItem> sacredItem = ITEMS.register("sacred_" + basename,
                () -> new BlockItem(sacred.get(), new Item.Properties().rarity(Rarity.RARE)));
        DeferredItem<BlockItem> megaItem = ITEMS.register("mega_" + basename,
                () -> new BlockItem(mega.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
        DeferredItem<BlockItem> massiveItem = ITEMS.register("massive_" + basename,
                () -> new FoiledBlockItem(massive.get(), new Item.Properties().rarity(Rarity.EPIC)));

        return new SaplingVariant(sacred, mega, massive, sacredItem, megaItem, massiveItem);
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
