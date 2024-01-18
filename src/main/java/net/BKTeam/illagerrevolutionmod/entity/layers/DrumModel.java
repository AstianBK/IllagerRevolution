package net.BKTeam.illagerrevolutionmod.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.BKTeam.illagerrevolutionmod.entity.custom.WildRavagerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
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

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        matrixStack.pushPose();
        translateAndRotate(matrixStack);
        root.render(matrixStack, buffer, packedLight, packedOverlay);
        matrixStack.popPose();
    }
    public void translateAndRotate(PoseStack pPoseStack) {
        pPoseStack.translate((double)(root.x / 16.0F), (double)(root.y / 16.0F), (double)(root.z / 16.0F));
        if (root.zRot != 0.0F) {
            pPoseStack.mulPose(Axis.ZP.rotation(root.zRot));
        }

        if (root.yRot != 0.0F) {
            pPoseStack.mulPose(Axis.YP.rotation(root.yRot));
        }

        if (root.xRot != 0.0F) {
            pPoseStack.mulPose(Axis.XP.rotation(root.xRot));
        }

    }
    @Override
    public void setupAnim(WildRavagerEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

    }
}

