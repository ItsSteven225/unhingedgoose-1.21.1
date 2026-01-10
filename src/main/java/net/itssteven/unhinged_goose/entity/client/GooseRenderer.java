package net.itssteven.unhinged_goose.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.itssteven.unhinged_goose.UnhingedGoose;
import net.itssteven.unhinged_goose.entity.custom.GooseEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GooseRenderer extends MobRenderer<GooseEntity, GooseModel<GooseEntity>> {
    public GooseRenderer(EntityRendererProvider.Context context) {
        super(context, new GooseModel<>(context.bakeLayer(GooseModel.LAYER_LOCATION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(GooseEntity gooseEntity) {
        return ResourceLocation.fromNamespaceAndPath(UnhingedGoose.MOD_ID, "textures/entity/goose/goose_white.png");
    }

    @Override
    public void render(GooseEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.45F, 0.45F, 0.45F);
        } else {
            poseStack.scale(1F, 1F, 1F);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
