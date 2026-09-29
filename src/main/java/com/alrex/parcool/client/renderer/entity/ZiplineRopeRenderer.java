package com.alrex.parcool.client.renderer.entity;

import com.alrex.parcool.client.renderer.RenderTypes;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ZiplineRopeRenderer
extends EntityRenderer<ZiplineRopeEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.parse((String)"textures/entity/arrow");

    public ZiplineRopeRenderer(EntityRendererProvider.Context p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Nonnull
    public ResourceLocation getTextureLocation(@Nonnull ZiplineRopeEntity ziplineRopeEntity) {
        return TEXTURE_LOCATION;
    }

    public boolean shouldRender(ZiplineRopeEntity entity, Frustum frustum, double x, double y, double z) {
        return entity.shouldRender(x, y, z);
    }

    public void render(@Nonnull ZiplineRopeEntity entity, float p_225623_2_, float partialTick, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource multiBufferSource, int p_225623_6_) {
        this.renderRope(entity, partialTick, matrixStack, multiBufferSource);
    }

    private void renderRope(ZiplineRopeEntity entity, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource) {
        BlockPos start = entity.getStartPos();
        BlockPos end = entity.getEndPos();
        if (start == BlockPos.ZERO && end == BlockPos.ZERO) {
            return;
        }
        int color = entity.getColor();
        float r = (float)((0xFF0000 & color) >> 16) / 255.0f;
        float g = (float)((0xFF00 & color) >> 8) / 255.0f;
        float b = (float)(0xFF & color) / 255.0f;
        Vec3 entityPos = entity.position();
        Zipline zipline = entity.getZipline();
        Vec3 startPos = zipline.getStartPos();
        Vec3 startPosOffset = startPos.subtract(entityPos);
        Vec3 endOffsetFromStart = zipline.getOffsetToEndFromStart();
        boolean render3d = ParCoolConfig.Client.Booleans.Enable3DRenderingForZipline.get();
        matrixStack.pushPose();
        matrixStack.translate(startPosOffset.x(), startPosOffset.y(), startPosOffset.z());
        VertexConsumer vertexConsumer = render3d ? multiBufferSource.getBuffer(RenderTypes.ZIPLINE_3D) : multiBufferSource.getBuffer(RenderTypes.ZIPLINE_2D);
        Matrix4f transformMatrix = matrixStack.last().pose();
        int startBlockLightLevel = this.getBlockLightLevel(entity, start);
        int endBlockLightLevel = this.getBlockLightLevel(entity, end);
        int startSkyBrightness = entity.level().getBrightness(LightLayer.SKY, start);
        int endSkyBrightness = entity.level().getBrightness(LightLayer.SKY, end);
        int divisionCount = Math.min((int)Math.ceil(endOffsetFromStart.length() / 0.6), 24);
        float invLengthSqrtXZ = (float)Mth.fastInvSqrt((double)(endOffsetFromStart.x() * endOffsetFromStart.x() + endOffsetFromStart.z() * endOffsetFromStart.z()));
        float unitLengthX = (float)(endOffsetFromStart.x() * (double)invLengthSqrtXZ);
        float unitLengthZ = (float)(endOffsetFromStart.z() * (double)invLengthSqrtXZ);
        for (int i = 0; i < divisionCount; ++i) {
            float colorScale = i % 2 == 0 ? 1.0f : 0.8f;
            for (int j = 0; j < 2; ++j) {
                if (render3d) {
                    this.renderRopeSingleBlock3D(transformMatrix, vertexConsumer, zipline, i, divisionCount, unitLengthX, unitLengthZ, startBlockLightLevel, endBlockLightLevel, startSkyBrightness, endSkyBrightness, r * colorScale, g * colorScale, b * colorScale);
                    continue;
                }
                this.renderRopeSingleBlock2D(transformMatrix, vertexConsumer, zipline, i, divisionCount, unitLengthX, unitLengthZ, startBlockLightLevel, endBlockLightLevel, startSkyBrightness, endSkyBrightness, r * colorScale, g * colorScale, b * colorScale, j % 2 == 0);
            }
        }
        matrixStack.popPose();
    }

    private void renderRopeSingleBlock2D(Matrix4f transformMatrix, VertexConsumer vertexConsumer, Zipline zipline, int currentCount, int maxCount, float unitLengthX, float unitLengthZ, int startBlockLightLevel, int endBlockLightLevel, int startSkyBrightness, int endSkyBrightness, float r, float g, float b, boolean tiltType) {
        for (int i = 0; i < 2; ++i) {
            float phase = (float)(currentCount + i) / (float)maxCount;
            int lightLevel = LightTexture.pack((int)((int)Mth.lerp((float)phase, (float)startBlockLightLevel, (float)endBlockLightLevel)), (int)((int)Mth.lerp((float)phase, (float)startSkyBrightness, (float)endSkyBrightness)));
            Vec3 midPointD = zipline.getMidPointOffsetFromStart(phase);
            Vector3f midPoint = new Vector3f((float)midPointD.x(), (float)midPointD.y(), (float)midPointD.z());
            float width = 0.075f;
            float tilt = zipline.getSlope(phase);
            float tiltInv = Mth.invSqrt((float)(tilt * tilt + 1.0f));
            float yOffset = 0.075f * tiltInv / 1.4142135f;
            float xBaseOffset = unitLengthX * 0.075f * tilt * tiltInv / 1.4142135f;
            float zBaseOffset = unitLengthZ * 0.075f * tilt * tiltInv / 1.4142135f;
            float sign = tiltType ? 1.0f : -1.0f;
            float xOffset = sign * unitLengthZ * 0.075f / 1.4142135f;
            float zOffset = sign * -unitLengthX * 0.075f / 1.4142135f;
            if (i == 0) {
                vertexConsumer.addVertex(transformMatrix, midPoint.x() + xBaseOffset + xOffset, midPoint.y() - yOffset, midPoint.z() + zBaseOffset + zOffset).setColor(r, g, b, 1.0f).setLight(lightLevel);
                vertexConsumer.addVertex(transformMatrix, midPoint.x() - xBaseOffset - xOffset, midPoint.y() + yOffset, midPoint.z() - zBaseOffset - zOffset).setColor(r, g, b, 1.0f).setLight(lightLevel);
                continue;
            }
            vertexConsumer.addVertex(transformMatrix, midPoint.x() - xBaseOffset - xOffset, midPoint.y() + yOffset, midPoint.z() - zBaseOffset - zOffset).setColor(r, g, b, 1.0f).setLight(lightLevel);
            vertexConsumer.addVertex(transformMatrix, midPoint.x() + xBaseOffset + xOffset, midPoint.y() - yOffset, midPoint.z() + zBaseOffset + zOffset).setColor(r, g, b, 1.0f).setLight(lightLevel);
        }
    }

    private void renderRopeSingleBlock3D(Matrix4f transformMatrix, VertexConsumer vertexConsumer, Zipline zipline, int currentCount, int maxCount, float unitLengthX, float unitLengthZ, int startBlockLightLevel, int endBlockLightLevel, int startSkyBrightness, int endSkyBrightness, float r, float g, float b) {
        int i;
        Vector3f[] vertexList = new Vector3f[8];
        int[] lightLevelList = new int[2];
        for (i = 0; i < 2; ++i) {
            float phase = (float)(currentCount + i) / (float)maxCount;
            lightLevelList[i] = LightTexture.pack((int)((int)Mth.lerp((float)phase, (float)startBlockLightLevel, (float)endBlockLightLevel)), (int)((int)Mth.lerp((float)phase, (float)startSkyBrightness, (float)endSkyBrightness)));
            Vec3 midPointD = zipline.getMidPointOffsetFromStart(phase);
            Vector3f midPoint = new Vector3f((float)midPointD.x(), (float)midPointD.y(), (float)midPointD.z());
            float width = 0.075f;
            float tilt = zipline.getSlope(phase);
            float tiltInv = Mth.invSqrt((float)(tilt * tilt + 1.0f));
            float yOffset = 0.075f * tiltInv / 1.4142135f;
            float xBaseOffset = unitLengthX * 0.075f * tilt * tiltInv / 1.4142135f;
            float zBaseOffset = unitLengthZ * 0.075f * tilt * tiltInv / 1.4142135f;
            float xOffset = unitLengthZ * 0.075f / 1.4142135f;
            float zOffset = -unitLengthX * 0.075f / 1.4142135f;
            vertexList[4 * i] = new Vector3f(midPoint.x() - xBaseOffset + xOffset, midPoint.y() + yOffset, midPoint.z() - zBaseOffset + zOffset);
            vertexList[4 * i + 1] = new Vector3f(midPoint.x() - xBaseOffset - xOffset, midPoint.y() + yOffset, midPoint.z() - zBaseOffset - zOffset);
            vertexList[4 * i + 2] = new Vector3f(midPoint.x() + xBaseOffset - xOffset, midPoint.y() - yOffset, midPoint.z() + zBaseOffset - zOffset);
            vertexList[4 * i + 3] = new Vector3f(midPoint.x() + xBaseOffset + xOffset, midPoint.y() - yOffset, midPoint.z() + zBaseOffset + zOffset);
        }
        for (i = 0; i < 4; ++i) {
            vertexConsumer.addVertex(transformMatrix, vertexList[i].x(), vertexList[i].y(), vertexList[i].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[0]);
            vertexConsumer.addVertex(transformMatrix, vertexList[(i + 1) % 4].x(), vertexList[(i + 1) % 4].y(), vertexList[(i + 1) % 4].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[0]);
            vertexConsumer.addVertex(transformMatrix, vertexList[4 + (i + 1) % 4].x(), vertexList[4 + (i + 1) % 4].y(), vertexList[4 + (i + 1) % 4].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[1]);
            vertexConsumer.addVertex(transformMatrix, vertexList[4 + i].x(), vertexList[4 + i].y(), vertexList[4 + i].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[1]);
        }
        if (currentCount == 0) {
            vertexConsumer.addVertex(transformMatrix, vertexList[3].x(), vertexList[3].y(), vertexList[3].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[0]);
            vertexConsumer.addVertex(transformMatrix, vertexList[2].x(), vertexList[2].y(), vertexList[2].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[0]);
            vertexConsumer.addVertex(transformMatrix, vertexList[1].x(), vertexList[1].y(), vertexList[1].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[0]);
            vertexConsumer.addVertex(transformMatrix, vertexList[0].x(), vertexList[0].y(), vertexList[0].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[0]);
        } else if (currentCount == maxCount - 1) {
            vertexConsumer.addVertex(transformMatrix, vertexList[4].x(), vertexList[4].y(), vertexList[4].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[0]);
            vertexConsumer.addVertex(transformMatrix, vertexList[5].x(), vertexList[5].y(), vertexList[5].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[0]);
            vertexConsumer.addVertex(transformMatrix, vertexList[6].x(), vertexList[6].y(), vertexList[6].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[0]);
            vertexConsumer.addVertex(transformMatrix, vertexList[7].x(), vertexList[7].y(), vertexList[7].z()).setColor(r, g, b, 1.0f).setLight(lightLevelList[0]);
        }
    }
}

