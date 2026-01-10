package net.itssteven.unhinged_goose.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.itssteven.unhinged_goose.UnhingedGoose;
import net.itssteven.unhinged_goose.entity.custom.GooseEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GooseModel<T extends GooseEntity> extends HierarchicalModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(UnhingedGoose.MOD_ID, "goose"), "main");
    private final ModelPart goose;
    private final ModelPart head;
    private final ModelPart wing1;
    private final ModelPart wing0;
    private final ModelPart leg0;
    private final ModelPart leg1;

    public GooseModel(ModelPart root) {
        this.goose = root.getChild("goose");
        this.head = this.goose.getChild("head");
        this.wing0 = this.goose.getChild("wing0");
        this.wing1 = this.goose.getChild("wing1");
        this.leg0 = this.goose.getChild("leg0");
        this.leg1 = this.goose.getChild("leg1");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition goose = partdefinition.addOrReplaceChild("goose", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = goose.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-2.0F, 3.0F, -1.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition head = goose.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-2.0F, -10.0F, -3.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -2.0F));

        PartDefinition beak = head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(28, 14).addBox(-2.0F, -5.0F, -4.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -2.0F));

        PartDefinition wing0 = goose.addOrReplaceChild("wing0", CubeListBuilder.create().texOffs(14, 13).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -11.0F, 0.0F));

        PartDefinition wing1 = goose.addOrReplaceChild("wing1", CubeListBuilder.create().texOffs(14, 23).addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -11.0F, 0.0F));

        PartDefinition leg0 = goose.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -5.0F, 1.0F));

        PartDefinition leg1 = goose.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(28, 6).addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -5.0F, 1.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(GooseEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        this.animate(entity.flyAnimationState, GooseAnimations.GOOSE_FLAP, ageInTicks, 1F);
        this.animateWalk(GooseAnimations.GOOSE_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.animate(entity.attackAnimationState, GooseAnimations.GOOSE_ATTACK, ageInTicks, 1f);
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45f);

        this.head.yRot = headYaw * ((float)Math.PI/ 180F);
        this.head.xRot = headPitch * ((float)Math.PI/ 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        goose.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return goose;
    }
}
