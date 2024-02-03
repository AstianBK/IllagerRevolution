package net.BKTeam.illagerrevolutionmod.data.server.tags;

import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import net.BKTeam.illagerrevolutionmod.entity.ModEntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;


public class BKEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public BKEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
        super(output,lookupProvider, IllagerRevolutionMod.MOD_ID, fileHelper);
    }

    protected void addTags() {
        this.tag(EntityTypeTags.ARROWS).add(ModEntityTypes.ARROWBEAST.get());
    }
}
