package io.bluebeaker.enchantedsacredtrees;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SacredTreesMod.MODID)
public class SacredTreesMod {

    static final String MODID = "sacred_trees";
    private static final Logger LOGGER = LogManager.getLogger();

    public SacredTreesMod(IEventBus modEventBus) {
        SaplingBlocks.BLOCKS.register(modEventBus);
        SaplingBlocks.ITEMS.register(modEventBus);
        SaplingBlocks.CREATIVE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }
}
