package net.BKTeam.illagerrevolutionmod.data.server.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class BkItemTagsProvider extends ItemTagsProvider {

    public BkItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, pBlockTagsProvider, IllagerRevolutionMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(ItemTags.ARROWS).add(ModItems.ARROW_BEAST.get());
    }
}
