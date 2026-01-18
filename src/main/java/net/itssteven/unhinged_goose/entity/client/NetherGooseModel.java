package net.itssteven.unhinged_goose.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.itssteven.unhinged_goose.UnhingedGoose;
import net.itssteven.unhinged_goose.entity.custom.NetherGooseEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class NetherGooseModel<T extends NetherGooseEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(UnhingedGoose.MOD_ID, "nether_goose"), "main");
    private final ModelPart nether_goose;
    private final ModelPart body;
    private final ModelPart wing_left;
    private final ModelPart wing_right;
    private final ModelPart head;
    private final ModelPart leg_right;
    private final ModelPart leg_left;

    public NetherGooseModel(ModelPart root) {
        this.nether_goose = root.getChild("nether_goose");
        this.body = this.nether_goose.getChild("body");
        this.wing_left = this.nether_goose.getChild("wing_left");
        this.wing_right = this.nether_goose.getChild("wing_right");
        this.head = this.nether_goose.getChild("head");
        this.leg_right = this.nether_goose.getChild("leg_right");
        this.leg_left = this.nether_goose.getChild("leg_left");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition nether_goose = partdefinition.addOrReplaceChild("nether_goose", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition body = nether_goose.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 0.0F, -9.0F, 16.0F, 14.0F, 17.0F, new CubeDeformation(0.02F))
                .texOffs(0, 89).addBox(0.0F, -6.0F, -5.0F, 0.0F, 6.0F, 12.0F, new CubeDeformation(0.02F))
                .texOffs(60, 31).addBox(-6.0F, 0.0F, 8.0F, 12.0F, 8.0F, 4.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition wing_left = nether_goose.addOrReplaceChild("wing_left", CubeListBuilder.create().texOffs(30, 31).addBox(0.0F, 0.0F, -7.0F, 2.0F, 10.0F, 13.0F, new CubeDeformation(0.02F)), PartPose.offset(8.0F, 2.0F, 0.0F));

        PartDefinition wing_right = nether_goose.addOrReplaceChild("wing_right", CubeListBuilder.create().texOffs(30, 54).addBox(-2.0F, 0.0F, -6.0F, 2.0F, 10.0F, 13.0F, new CubeDeformation(0.02F)), PartPose.offset(-8.0F, 2.0F, -1.0F));

        PartDefinition head = nether_goose.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 60).addBox(-4.0F, -19.0F, -13.0F, 8.0F, 4.0F, 6.0F, new CubeDeformation(0.02F))
                .texOffs(0, 31).addBox(-4.0F, -22.0F, -7.0F, 8.0F, 22.0F, 7.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, 6.0F, -5.0F));

        PartDefinition leg_right = nether_goose.addOrReplaceChild("leg_right", CubeListBuilder.create().texOffs(60, 43).addBox(-3.0F, 14.0F, -7.0F, 5.0F, 0.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(60, 65).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 11.0F, 0.0F));

        PartDefinition leg_left = nether_goose.addOrReplaceChild("leg_left", CubeListBuilder.create().texOffs(60, 54).addBox(-2.0F, 14.0F, -7.0F, 5.0F, 0.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(66, 0).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 11.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(NetherGooseEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

        boolean inLiquid = entity.isInWater() || entity.isInLava();
        boolean attacking1 = entity.attack1AnimationState.isStarted();
        boolean attacking2 = entity.attack2AnimationState.isStarted();
        boolean attacking = attacking1 || attacking2;
        boolean flying = !entity.onGround() && !inLiquid;

        if (!attacking) {
            this.root().getAllParts().forEach(ModelPart::resetPose);
        }

        if (!attacking) {
            this.applyHeadRotation(netHeadYaw, headPitch);
        }

        if (attacking2) {
            this.animate(entity.attack2AnimationState, NetherGooseAnimations.NETHER_GOOSE_ATTACK2, ageInTicks, 1f);
            return;
        }

        if (attacking1) {
            this.animate(entity.attack1AnimationState, NetherGooseAnimations.NETHER_GOOSE_ATTACK, ageInTicks, 1f);
            return;
        }

        if (inLiquid) {
            this.leg_left.xRot = 0.0F;
            this.leg_right.xRot = 0.0F;

            this.animate(entity.swimAnimationState,     NetherGooseAnimations.NETHER_GOOSE_LAVA_SWIM, ageInTicks, 1f);
            return;
        }

        if (flying) {
            this.animate(entity.flyAnimationState, NetherGooseAnimations.NETHER_GOOSE_FLAP, ageInTicks, 1f);
            return;
        }

        this.animateWalk(GooseAnimations.GOOSE_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
    }



    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45f);

        this.head.yRot = headYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        nether_goose.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return nether_goose;
    }
}