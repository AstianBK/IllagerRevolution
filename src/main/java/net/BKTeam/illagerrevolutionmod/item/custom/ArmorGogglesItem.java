package net.BKTeam.illagerrevolutionmod.item.custom;

import com.google.common.collect.ImmutableMap;
import net.BKTeam.illagerrevolutionmod.enchantment.InitEnchantment;
import net.BKTeam.illagerrevolutionmod.item.ModArmorMaterials;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;

public class ArmorGogglesItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final Map<ArmorMaterial, MobEffectInstance> MATERIAL_TO_EFFECT_MAP =
            (new ImmutableMap.Builder<ArmorMaterial, MobEffectInstance>())
                    .put(ModArmorMaterials.ILLAGIUM, new MobEffectInstance(MobEffects.UNLUCK, 0, 0)).build();

    public ArmorGogglesItem(ModArmorMaterials material, ArmorItem.Type slot, Properties settings) {
        super(material, slot, settings);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<ArmorGogglesItem>(this, "controller",
                20, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private <P extends GeoItem> PlayState predicate(AnimationState<P> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        if (!level.isClientSide()) {
            double cc=20.0d;
            if(player.hasEffect(MobEffects.BLINDNESS)){
                player.removeEffect(MobEffects.BLINDNESS);
            }
            cc+=5*EnchantmentHelper.getItemEnchantmentLevel(InitEnchantment.WARY_LENSES.get(),stack);
            if(level.getMaxLocalRawBrightness(player.blockPosition())<=1){
                player.level().getEntitiesOfClass(Monster.class,player.getBoundingBox().inflate(cc)).forEach(entity ->{
                    if(!entity.hasEffect(MobEffects.GLOWING)){
                        entity.addEffect(new MobEffectInstance(MobEffects.GLOWING,20,0));
                    }
                });
            }
        }
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    }

}