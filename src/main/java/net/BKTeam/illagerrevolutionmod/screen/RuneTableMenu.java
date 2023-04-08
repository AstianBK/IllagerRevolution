package net.BKTeam.illagerrevolutionmod.screen;

import net.BKTeam.illagerrevolutionmod.block.ModBlocks;
import net.BKTeam.illagerrevolutionmod.block.entity.custom.RuneTableEntity;
import net.BKTeam.illagerrevolutionmod.item.ModItems;
import net.BKTeam.illagerrevolutionmod.item.custom.RunedSword;
import net.BKTeam.illagerrevolutionmod.item.custom.SwordRuneBladeItem;
import net.BKTeam.illagerrevolutionmod.screen.slot.ModResultSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class RuneTableMenu extends AbstractContainerMenu {
    private final Level level;
    private final CraftingContainer core=new CraftingContainer(this,2,1);
    ContainerLevelAccess callable;
    private Player player;
    private ResultContainer resultContainer;

    public RuneTableMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv,ContainerLevelAccess.NULL );
    }

    public RuneTableMenu(int pContainerId, Inventory inv,ContainerLevelAccess callable) {
        super(ModMenuTypes.RUNE_TABLE_MENU.get(), pContainerId);
        checkContainerSize(inv, 4);
        this.callable = callable;
        this.level = inv.player.level;
        this.player=inv.player;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new Slot(this.core, 0, 17, 40){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(ModItems.RUNE_TABLET_UNDYING_BONE.get()) || stack.is(ModItems.RUNE_TABLET_UNDYING_FLESH.get());
            }
        });
        this.addSlot(new Slot(this.core, 1, 17, 17){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                    return stack.is(ModItems.RUSTIC_CHISEL.get());
                }
        });
        this.addSlot(new Slot(this.core, 2, 77, 17){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                    return stack.getItem() instanceof RunedSword;
                }
        });
        this.addSlot(new ModResultSlot(inv.player,core,inv , 3, 80, 60));

    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 3;

    protected void updateCraftingResult(int id, Level level, Player player, CraftingContainer pCore, ResultContainer resultContainer){
        if(!level.isClientSide){
            ServerPlayer serverPlayer=(ServerPlayer) player;
            ItemStack result=ItemStack.EMPTY;
            recipeResult(pCore);
            result=recipeResult(pCore);
            resultContainer.setItem(3,result);
        }
    }

    @Override
    public void slotsChanged(Container pInventory) {
        callable.execute((lol,los) ->{
            updateCraftingResult(this.containerId,lol,player,core,resultContainer);
        });
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        callable.execute((l,r)->{
            this.clearContainer(pPlayer,this.core);
        });
    }

    private ItemStack recipeResult(CraftingContainer pCore){
        return new ItemStack(pCore.getItem(2).getItem() instanceof SwordRuneBladeItem ? ModItems.ILLAGIUM_ALT_RUNED_BLADE.get() : ModItems.ILLAGIUM_RUNED_BLADE.get());

    }
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {

            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.callable ,
                pPlayer, ModBlocks.RUNE_TABLE_BLOCK.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }
}
