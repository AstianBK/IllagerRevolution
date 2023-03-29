package net.BKTeam.illagerrevolutionmod.data.server.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.BKTeam.illagerrevolutionmod.IllagerRevolutionMod;
import org.jetbrains.annotations.Nullable;

public class BkBlockTagsProvider extends BlockTagsProvider {
    public BkBlockTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, IllagerRevolutionMod.MOD_ID, existingFileHelper);
    }
}
