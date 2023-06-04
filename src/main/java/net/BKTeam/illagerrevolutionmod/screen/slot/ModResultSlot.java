package net.BKTeam.illagerrevolutionmod.screen.slot;
import net.BKTeam.illagerrevolutionmod.block.entity.custom.RuneTableEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.RunedSword;
import net.BKTeam.illagerrevolutionmod.sound.ModSounds;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ModResultSlot extends Slot {
    CraftingContainer core;
    private final Player player;
    private int amountCrafted;

    public ModResultSlot(Player player,CraftingContainer core,Container container, int index, int x, int y) {
        super(container, index, x, y);
        this.player=player;
        this.core=core;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack remove(int pAmount) {
        if(this.hasItem()){
            this.amountCrafted += pAmount;
        }
        return super.remove(pAmount);
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    protected void onSwapCraft(int pNumItemsCrafted) {
        this.amountCrafted+=pNumItemsCrafted;
    }

    @Override
    protected void onQuickCraft(ItemStack pStack, int pAmount) {
        this.amountCrafted += pAmount;
        this.checkTakeAchievements(pStack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack pStack) {
        if(this.amountCrafted > 0){
            pStack.onCraftedBy(this.player.level,this.player,this.amountCrafted);
        }
        player.playSound(ModSounds.RUNE_TABLE_USE.get(),1.0f,1.0f);
        super.checkTakeAchievements(pStack);
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {
        this.checkTakeAchievements(pStack);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(pPlayer);
        if(hasRecipe() && hasNotReachedStackLimit()){
            ItemStack stack=core.getItem(0);
            ItemStack stack1=core.getItem(1);
            ItemStack stack2=core.getItem(2);
            stack.shrink(1);
            stack1.shrink(1);
            stack2.shrink(1);
        }
        super.onTake(pPlayer, pStack);
    }

    private boolean hasRecipe() {
        boolean hasItemInZeroSlot = core.getItem(0).getItem() == ModItems.RUNE_TABLET_UNDYING_BONE.get() || core.getItem(0).getItem() == ModItems.RUNE_TABLET_UNDYING_FLESH.get();
        boolean hasItemInFirstSlot = core.getItem(1).getItem() == Items.LAPIS_LAZULI;
        boolean hasItemInSecondSlot = core.getItem(2).getItem() instanceof RunedSword;

        return hasItemInZeroSlot && hasItemInFirstSlot && hasItemInSecondSlot;
    }

    private boolean hasNotReachedStackLimit() {
        return core.getItem(3).getCount() < core.getItem(3).getMaxStackSize();
    }

}
