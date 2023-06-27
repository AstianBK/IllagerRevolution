package net.BKTeam.illagerrevolutionmod.gui;

import net.BKTeam.illagerrevolutionmod.entity.custom.MaulerEntity;
import net.BKTeam.illagerrevolutionmod.entity.custom.ScroungerEntity;
import net.BKTeam.illagerrevolutionmod.item.Beast;
import net.BKTeam.illagerrevolutionmod.item.custom.BeastArmorItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SaddleItem;
import net.minecraft.world.item.alchemy.PotionUtils;

public class ScroungerInventoryMenu extends AbstractContainerMenu {
    private final Container maulerContainer;
    private final ScroungerEntity scrounger;

    public ScroungerInventoryMenu(int p_39656_, Inventory p_39657_, Container p_39658_, final ScroungerEntity p_39659_) {
        super((MenuType<?>) null, p_39656_);
        this.maulerContainer = p_39658_;
        this.scrounger = p_39659_;
        p_39658_.startOpen(p_39657_.player);
        for(int l = 0; l < 2; ++l) {
            int finalL = l;
            this.addSlot(new Slot(p_39658_, finalL, 80 + (l * 18), 18){
                @Override
                public boolean mayPlace(ItemStack pStack) {
                    return super.mayPlace(pStack) && canUsedPotion(finalL,pStack);
                }

                @Override
                public boolean isActive() {
                    return hasChest();
                }
            });
        }

        for(int i1 = 0; i1 < 3; ++i1) {
            for(int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(p_39657_, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
            }
        }

        for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(p_39657_, j1, 8 + j1 * 18, 142));
        }

    }

    public boolean stillValid(Player pPlayer) {
        return !this.scrounger.hasInventoryChanged(this.maulerContainer) && this.maulerContainer.stillValid(pPlayer) && this.scrounger.isAlive() && this.scrounger.distanceTo(pPlayer) < 8.0F;
    }

    public boolean canUsedPotion(int pSlot, ItemStack pStack){
        return (pStack.is(Items.POTION)) && isBeneficalPotion(pSlot,pStack);
    }

    public boolean isBeneficalPotion(int pSlot,ItemStack pStack){
        if(pSlot==0){
            return PotionUtils.getPotion(pStack).getEffects().stream().anyMatch(e-> e.getEffect().isBeneficial());
        }else {
            return PotionUtils.getPotion(pStack).getEffects().stream().anyMatch(e-> !e.getEffect().isBeneficial());
        }
    }

    public boolean hasChest(){
        return this.scrounger.hasChest();
    }
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int i = this.maulerContainer.getContainerSize();
            if (pIndex < i) {
                if (!this.moveItemStackTo(itemstack1, i, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }else if (i <= 0 || !this.moveItemStackTo(itemstack1, 0, i, false)) {
                int j = i + 27;
                int k = j + 9;
                if (pIndex >= j && pIndex < k) {
                    if (!this.moveItemStackTo(itemstack1, i, j, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= i && pIndex < j) {
                    if (!this.moveItemStackTo(itemstack1, j, k, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, j, j, false)) {
                    return ItemStack.EMPTY;
                }

                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.maulerContainer.stopOpen(pPlayer);
    }
}
