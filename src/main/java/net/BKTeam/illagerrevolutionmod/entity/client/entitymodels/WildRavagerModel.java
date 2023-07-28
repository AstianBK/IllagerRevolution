package net.BKTeam.illagerrevolutionmod.entity.client.entitymodels;

import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class WildRavagerModel extends HierarchicalModel<WildRavagerEntity> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart mouth;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart neck;

    private final ModelPart body;

    private int prepareTick;

    public WildRavagerModel(ModelPart pRoot) {
        this.root = pRoot;
        this.neck = pRoot.getChild("neck");
        this.body = pRoot.getChild("body");
        this.head = this.neck.getChild("head");
        this.mouth = this.head.getChild("mouth");
        this.rightHindLeg = pRoot.getChild("right_hind_leg");
        this.leftHindLeg = pRoot.getChild("left_hind_leg");
        this.rightFrontLeg = pRoot.getChild("right_front_leg");
        this.leftFrontLeg = pRoot.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        int i = 16;
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F), PartPose.offset(0.0F, -7.0F, 5.5F));
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F).texOffs(0, 0).addBox(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F), PartPose.offset(0.0F, 16.0F, -17.0F));
        partdefinition2.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F), PartPose.offsetAndRotation(-10.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F));
        partdefinition2.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(74, 55).mirror().addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F), PartPose.offsetAndRotation(8.0F, -14.0F, -8.0F, 1.0995574F, 0.0F, 0.0F));
        partdefinition2.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F), PartPose.offset(0.0F, -2.0F, 2.0F));
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 55).addBox(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F).texOffs(0, 91).addBox(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(-8.0F, -13.0F, 18.0F));
        partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(96, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(8.0F, -13.0F, 18.0F));
        partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(64, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(-8.0F, -13.0F, -5.0F));
        partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(64, 0).mirror().addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F), PartPose.offset(8.0F, -13.0F, -5.0F));
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public ModelPart root() {
        return this.root;
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setupAnim(WildRavagerEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        float f = 0.4F * pLimbSwingAmount;
        this.rightHindLeg.xRot =!pEntity.isSitting() ? Mth.cos(pLimbSwing * 0.6662F) * f: ((float)Math.PI * 1.5708F);
        this.leftHindLeg.xRot =!pEntity.isSitting() ? Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * f : (float)Math.PI * 1.5708F;
        this.rightFrontLeg.xRot =!pEntity.isSitting() ? Mth.cos(pLimbSwing * 0.6662F + (float)Math.PI) * f : -1.23446F;
        this.leftFrontLeg.xRot = !pEntity.isSitting() ? Mth.cos(pLimbSwing * 0.6662F) * f : -1.23446F;
    }

    public void prepareMobModel(@NotNull WildRavagerEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        super.prepareMobModel(pEntity,pLimbSwing,pLimbSwingAmount,pPartialTick);
        int i = pEntity.getStunnedTick();
        int j = pEntity.getRoarTick();
        int k = 20;
        int l = pEntity.getAttackTick();
        int i1 = 10;
        if(pEntity.isSitting()){
            float f1 = Mth.cos(pEntity.tickCount/5.0F)*0.01F;
            this.body.xScale=1.0F + f1;
            this.body.yScale=1.0F + f1;
            this.body.zScale=1.0F + f1;
            this.body.y=14.5F;
            this.rightHindLeg.y=13.5F;
            this.leftHindLeg.y=13.5F;
            this.rightFrontLeg.y=5.5F;
            this.rightFrontLeg.x=-9.0F;
            this.rightFrontLeg.z=5.0F;
            this.leftFrontLeg.y=5.5F;
            this.leftFrontLeg.x=9.0F;
            this.leftFrontLeg.z=5.0F;
            this.rightHindLeg.yRot = 0.56732F;
            this.leftHindLeg.yRot =  -0.56732F;
            this.rightFrontLeg.yRot = 0.3926991F;
            this.leftFrontLeg.yRot = -0.3926991F;
        }else {
            this.body.xRot= (float) (Math.PI/2F);
            this.body.xScale=1.0F;
            this.body.yScale=1.0F;
            this.body.zScale=1.0F;
            this.body.y=1.0F;
            this.rightHindLeg.yRot = 0.0F;
            this.leftHindLeg.yRot =  0.0F;
            this.rightFrontLeg.yRot =0.0F ;
            this.leftFrontLeg.yRot = 0.0F;
            this.rightHindLeg.y=-13.0F;
            this.leftHindLeg.y=-13.0F;
            this.rightFrontLeg.setPos(-8.0f,-13.0F,-5.0F);
            this.leftFrontLeg.setPos(8.0f,-13.0F,-5.0F);
        }
        if (l > 0) {
            float f = Mth.triangleWave((float)l - pPartialTick, 10.0F);
            float f1 = (1.0F + f) * 0.5F;
            float f2 = f1 * f1 * f1 * 12.0F;
            float f3 = f2 * Mth.sin(this.neck.xRot);
            this.neck.z = -6.5F + f2;
            this.neck.y = -7.0F - f3;
            float f4 = Mth.sin(((float)l - pPartialTick) / 10.0F * (float)Math.PI * 0.25F);
            this.mouth.xRot = ((float)Math.PI / 2F) * f4;
            if (l > 5) {
                this.mouth.xRot = Mth.sin(((float)(-4 + l) - pPartialTick) / 4.0F) * (float)Math.PI * 0.4F;
            } else {
                this.mouth.xRot = 0.15707964F * Mth.sin((float)Math.PI * ((float)l - pPartialTick) / 10.0F);
            }
        } else {
            float f5 = -1.0F;
            float f6 = -1.0F * Mth.sin(this.neck.xRot);
            this.neck.x = 0.0F;
            this.neck.y = pEntity.isSitting() ? 3.0F - f6 :-7.0F - f6;
            this.neck.z = 5.5F;
            boolean flag = i > 0;
            this.neck.xRot = flag ? 0.21991149F : 0.0F;
            this.mouth.xRot = (float)Math.PI * (flag ? 0.05F : 0.01F);
            if (flag) {
                double d0 = (double)i / 40.0D;
                this.neck.x = (float)Math.sin(d0 * 10.0D) * 3.0F;
            } else if (j > 0) {
                float f7 = Mth.sin(((float)(20 - j) - pPartialTick) / 20.0F * (float)Math.PI * 0.25F);
                this.mouth.xRot = ((float)Math.PI / 2F) * f7;
            }
        }
        int pI = pEntity.getPrepareTimer();
        if(pI>0){
            float time =((float) (20 - pI))/20F;
            float f2 = -13.0F * time;
            float f22 = -13.0F * ((float) pI/20F);
            float f3 = Mth.cos(-60+(((float) (20 - pI))/20F)*400)*5;
            float f4 = Mth.sin( ((float) Math.PI * 2.0F)*time);
            this.leftFrontLeg.xRot = 0.698132F*f4;
            this.leftFrontLeg.y = Mth.lerp(time,-13.0F - 15.0F * time,-13.0F - 15.0F * ((float) pI/20F));
            this.leftFrontLeg.x = Mth.lerp(time,8.0f + 10.0F * time,8.0f + 10.0F * ((float) pI/20F));
            this.leftFrontLeg.z = Mth.lerp(time,-5.0F + 10.0F * time,-5.0F + 10.0F * ((float) pI/20F));
            this.rightFrontLeg.xRot = 0.698132F*f4;
            this.rightFrontLeg.y = Mth.lerp(time,-13.0F - 15.0F * time,-13.0F - 15.0F * ((float) pI/20F));
            this.rightFrontLeg.x = Mth.lerp(time,-8.0f - 10.0F * time,-8.0f - 10.0F * ((float) pI/20F));
            this.rightFrontLeg.z = Mth.lerp(time,-5.0F + 15.0F * time,-5.0F + 15.0F * ((float) pI/20F));
            if(pI>15){
                float f5 = Mth.sin( ((float)(20-pI) / 4.0F * (float) Math.PI * 0.5F));
                this.body.xRot = ((float)Math.PI / 2F) - 0.698132F*f5;
                this.neck.xRot = -0.698132F*f5;
                this.mouth.xRot = ((float)Math.PI / 4F)*f5;
            }else if(pI<10)  {
                float f5 = Mth.sin( ((float) (pI)) / 9.0F * (float) Math.PI * 0.5F);
                float f7 = Mth.sin( ((float) (9-pI)) / 9.0F * (float) Math.PI * 0.5F);
                this.body.xRot = (((float)Math.PI / 2F)) - (0.698132F*f5);
                if (pI<5) {
                    float f6 = Mth.sin(((float) (pI)) / 4.0F * (float) Math.PI * 0.5F);
                    float f8 = Mth.sin(((float) (4-pI)) / 4.0F * (float) Math.PI * 0.5F);
                    this.neck.xRot = -0.698132F*f6;
                    this.mouth.xRot = ((float)Math.PI / 4F) - 0.174533F - 0.698132F*f8;
                }else {
                    this.mouth.xRot = ((float)Math.PI / 4F) - 0.174533F*f7;
                }
            }
            this.neck.y = Mth.lerp(time,-7.0F - 4.0F * time,-7.0f - 4.0F * ((float) pI/20F));
            this.neck.z = Mth.lerp(time,5.5F - 2.0F * time,5.5f - 2.0F * ((float) pI/20F));
            this.body.y = Mth.lerp(time,f2,f22);
        }
    }
}
