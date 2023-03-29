package net.BKTeam.illagerrevolutionmod.data.server.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;


public class BKEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public BKEntityTypeTagsProvider(DataGenerator pGenerator, ExistingFileHelper fileHelper) {
        super(pGenerator, IllagerRevolutionMod.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(EntityTypeTags.RAIDERS).add(ModEntityTypes.ILLAGERMINER.get());
        this.tag(EntityTypeTags.ARROWS).add(ModEntityTypes.ARROWBEAST.get());    }
}
