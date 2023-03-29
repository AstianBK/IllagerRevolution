package net.BKTeam.illagerrevolutionmod.gui;

import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.BKTeam.illagerrevolutionmod.entity.custom.RakerEntity;
import net.BKTeam.illagerrevolutionmod.item.custom.RakerArmorItem;

public class RakerInventoryMenu extends AbstractContainerMenu {
    private final Container rakerContainer;
    private final RakerEntity raker;

    public RakerInventoryMenu(int p_39656_, Inventory p_39657_, Container p_39658_, final RakerEntity p_39659_) {
        super((MenuType<?>) null, p_39656_);
        this.rakerContainer = p_39658_;
        this.raker = p_39659_;
        p_39658_.startOpen(p_39657_.player);
        this.addSlot(new Slot(p_39658_, 0, 8, 54) {
            public boolean mayPlace(ItemStack p_39690_) {
                return false;
            }
            public boolean isActive() {
                return false;
            }

            public int getMaxStackSize() {
                return 0;
            }
        });
        this.addSlot(new Slot(p_39658_, 1, 8, 54) {
            public boolean mayPlace(ItemStack p_39690_) {
                return p_39690_.getItem() instanceof RakerArmorItem rakerArmorItem && rakerArmorItem.getEquipmetSlot().equals(EquipmentSlot.LEGS);
            }
            public boolean isActive() {
                return true;
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(p_39658_, 2, 8, 36) {
            public boolean mayPlace(ItemStack p_39690_) {
                return p_39690_.getItem() instanceof RakerArmorItem rakerArmorItem && rakerArmorItem.getEquipmetSlot()==EquipmentSlot.CHEST;
            }
            public boolean isActive() {
                return true;
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
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
        return !this.raker.hasInventoryChanged(this.rakerContainer) && this.rakerContainer.stillValid(pPlayer) && this.raker.isAlive() && this.raker.distanceTo(pPlayer) < 8.0F;
    }
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int i = this.rakerContainer.getContainerSize();
            if (pIndex < i) {
                if (!this.moveItemStackTo(itemstack1, i, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(2).mayPlace(itemstack1) && !this.getSlot(2).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 2, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
                    if (!this.moveItemStackTo(itemstack1, 1,2, false)) {
                        return ItemStack.EMPTY;
                    }
            }else if (this.getSlot(0).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 0,1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i <= 3 || !this.moveItemStackTo(itemstack1, 3, i, false)) {
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
        this.rakerContainer.stopOpen(pPlayer);
    }
}
