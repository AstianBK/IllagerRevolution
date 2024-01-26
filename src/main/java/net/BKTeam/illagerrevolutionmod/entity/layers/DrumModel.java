package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrumModel extends EntityModel<WildRavagerEntity> {

    private final ModelPart root;

    public DrumModel(ModelPart root) {
        this.root = root.getChild("drum");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("drum", CubeListBuilder.create().texOffs(0, 0).addBox(-5, 0, -4, 16.0F, 16.0F, 16.0F), PartPose.offsetAndRotation(0.0f, -20f, 0.0f, 0.0F, 0.0F, (float) Math.PI));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void renderOnShoulder(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float p_228284_5_, float p_228284_6_, float p_228284_7_, float p_228284_8_, int p_228284_9_) {
        root.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setupAnim(WildRavagerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){

    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        root.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}

