package dev.denimred.blockmod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import dev.denimred.blockmod.BlockHelper.RenderStyle;
import dev.denimred.blockmod.config.BlockModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlockMod.MOD_ID, value = Dist.CLIENT)
public final class ClientForgeEventListener {
    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        RenderStyle renderStyle = BlockModConfig.CLIENT.renderStyle.get();
        if (renderStyle != RenderStyle.SKIN) {
            AbstractClientPlayer player = (AbstractClientPlayer) event.getPlayer();
            ResourceLocation texture = BlockHelper.getBillboardTexture(player.getPlayerInfo());
            if (texture != null) {
                event.setCanceled(true);
                if (renderStyle.isVisible()) {
                    renderBillboard(event, player, texture);
                }
            }
        }
    }

    private static void renderBillboard(RenderPlayerEvent.Pre event, AbstractClientPlayer player, ResourceLocation texture) {
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();
        int light = event.getPackedLight();
        // Render the billboard itself
        poseStack.pushPose();
        poseStack.scale(2f, 2f, 2f);
        poseStack.translate(0, 0.5f, 0);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        PoseStack.Pose last = poseStack.last();
        Matrix4f pose = last.pose();
        Matrix3f normal = last.normal();
        VertexConsumer buffer = buffers.getBuffer(RenderType.entityTranslucentCull(texture));
        billboardVertex(buffer, pose, normal, light, 1, 0, 0, 1);
        billboardVertex(buffer, pose, normal, light, 0, 0, 1, 1);
        billboardVertex(buffer, pose, normal, light, 0, 1, 1, 0);
        billboardVertex(buffer, pose, normal, light, 1, 1, 0, 0);
        poseStack.popPose();
        // Render the nameplate
        PlayerRenderer renderer = event.getRenderer();
        RenderNameplateEvent nameplateEvent = new RenderNameplateEvent(player, player.getDisplayName(), renderer, poseStack, buffers, light, event.getPartialTick());
        MinecraftForge.EVENT_BUS.post(nameplateEvent);
        if (nameplateEvent.getResult() != Event.Result.DENY && (nameplateEvent.getResult() == Event.Result.ALLOW || renderer.shouldShowName(player))) {
            renderer.renderNameTag(player, nameplateEvent.getContent(), poseStack, buffers, light);
        }
    }

    private static void billboardVertex(VertexConsumer buffer, Matrix4f pose, Matrix3f normal, int light, float x, int y, int u, int v) {
        buffer.vertex(pose, x - 0.5f, y - 0.5f, 0.0f)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0f, 1.0f, 0.0f)
                .endVertex();
    }
}
