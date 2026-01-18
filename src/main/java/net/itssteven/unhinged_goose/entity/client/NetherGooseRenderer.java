package net.itssteven.unhinged_goose.entity.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.itssteven.unhinged_goose.UnhingedGoose;
import net.itssteven.unhinged_goose.entity.NetherGooseVariant;
import net.itssteven.unhinged_goose.entity.custom.NetherGooseEntity;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class NetherGooseRenderer extends MobRenderer<NetherGooseEntity, NetherGooseModel<NetherGooseEntity>> {
    private static final Map<NetherGooseVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(NetherGooseVariant.class), map -> {
                map.put(NetherGooseVariant.WHITE,
                        ResourceLocation.fromNamespaceAndPath(
                                UnhingedGoose.MOD_ID, "textures/entity/nether_goose/nether_goose_white.png"));

                map.put(NetherGooseVariant.BLACK,
                        ResourceLocation.fromNamespaceAndPath(
                                UnhingedGoose.MOD_ID, "textures/entity/nether_goose/nether_goose_black.png"));
            });

    public NetherGooseRenderer(EntityRendererProvider.Context context) {
        super(context, new NetherGooseModel<>(context.bakeLayer(NetherGooseModel.LAYER_LOCATION)), 0.9F);
    }

    @Override
    public ResourceLocation getTextureLocation(NetherGooseEntity netherGooseEntity) {
        return LOCATION_BY_VARIANT.get(netherGooseEntity.getVariant());
    }

    @Override
    public void render(NetherGooseEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.45F, 0.45F, 0.45F);
        } else {
            poseStack.scale(1F, 1F, 1F);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
