package com.alrex.parcool.client.renderer.entity;

import com.alrex.parcool.client.renderer.RenderTypes;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

/**
 * Minecraft 26.1 entity rendering uses an extract/submit pipeline.  Keep all
 * world/entity reads in extractRenderState, then submit immutable geometry for
 * the feature renderer to draw later in the frame.
 */
public class ZiplineRopeRenderer
        extends EntityRenderer<ZiplineRopeEntity, ZiplineRopeRenderer.ZiplineRopeRenderState> {

    public ZiplineRopeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ZiplineRopeRenderState createRenderState() {
        return new ZiplineRopeRenderState();
    }

    @Override
    public boolean shouldRender(ZiplineRopeEntity entity, Frustum frustum, double x, double y, double z) {
        return entity.shouldRender(x, y, z);
    }

    @Override
    public void extractRenderState(ZiplineRopeEntity entity, ZiplineRopeRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        BlockPos start = entity.getStartPos();
        BlockPos end = entity.getEndPos();
        state.valid = !(start.equals(BlockPos.ZERO) && end.equals(BlockPos.ZERO));
        if (!state.valid) {
            state.zipline = null;
            return;
        }

        Zipline zipline = entity.getZipline();
        state.zipline = zipline;
        state.startOffset = zipline.getStartPos().subtract(entity.position());
        state.render3d = ParCoolConfig.Client.Booleans.Enable3DRenderingForZipline.get();

        int color = entity.getColor();
        state.red = (float)((color >> 16) & 0xFF) / 255.0F;
        state.green = (float)((color >> 8) & 0xFF) / 255.0F;
        state.blue = (float)(color & 0xFF) / 255.0F;

        state.startBlockLight = this.getBlockLightLevel(entity, start);
        state.endBlockLight = this.getBlockLightLevel(entity, end);
        state.startSkyLight = this.getSkyLightLevel(entity, start);
        state.endSkyLight = this.getSkyLightLevel(entity, end);
    }

    @Override
    public void submit(ZiplineRopeRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        if (!state.valid || state.zipline == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(state.startOffset.x(), state.startOffset.y(), state.startOffset.z());

        RenderType renderType = state.render3d ? RenderTypes.ZIPLINE_3D : RenderTypes.ZIPLINE_2D;
        collector.submitCustomGeometry(poseStack, renderType,
                (pose, vertexConsumer) -> this.renderRopeGeometry(state, pose.pose(), vertexConsumer));

        poseStack.popPose();
    }

    private void renderRopeGeometry(ZiplineRopeRenderState state, Matrix4fc transformMatrix,
                                    VertexConsumer vertexConsumer) {
        Zipline zipline = state.zipline;
        Vec3 endOffsetFromStart = zipline.getOffsetToEndFromStart();
        int divisionCount = Math.min((int)Math.ceil(endOffsetFromStart.length() / 0.6), 24);
        if (divisionCount <= 0) {
            return;
        }

        double horizontalLengthSqr = endOffsetFromStart.x() * endOffsetFromStart.x()
                + endOffsetFromStart.z() * endOffsetFromStart.z();
        float invLengthSqrtXZ = horizontalLengthSqr > 1.0E-8
                ? (float)Mth.fastInvSqrt(horizontalLengthSqr)
                : 0.0F;
        float unitLengthX = (float)(endOffsetFromStart.x() * invLengthSqrtXZ);
        float unitLengthZ = (float)(endOffsetFromStart.z() * invLengthSqrtXZ);

        for (int i = 0; i < divisionCount; ++i) {
            float colorScale = i % 2 == 0 ? 1.0F : 0.8F;
            if (state.render3d) {
                renderRopeSingleBlock3D(transformMatrix, vertexConsumer, zipline, i, divisionCount,
                        unitLengthX, unitLengthZ,
                        state.startBlockLight, state.endBlockLight,
                        state.startSkyLight, state.endSkyLight,
                        state.red * colorScale, state.green * colorScale, state.blue * colorScale);
            } else {
                renderRopeSingleBlock2D(transformMatrix, vertexConsumer, zipline, i, divisionCount,
                        unitLengthX, unitLengthZ,
                        state.startBlockLight, state.endBlockLight,
                        state.startSkyLight, state.endSkyLight,
                        state.red * colorScale, state.green * colorScale, state.blue * colorScale, true);
                renderRopeSingleBlock2D(transformMatrix, vertexConsumer, zipline, i, divisionCount,
                        unitLengthX, unitLengthZ,
                        state.startBlockLight, state.endBlockLight,
                        state.startSkyLight, state.endSkyLight,
                        state.red * colorScale, state.green * colorScale, state.blue * colorScale, false);
            }
        }
    }

    private static void renderRopeSingleBlock2D(Matrix4fc transformMatrix, VertexConsumer vertexConsumer,
                                                 Zipline zipline, int currentCount, int maxCount,
                                                 float unitLengthX, float unitLengthZ,
                                                 int startBlockLightLevel, int endBlockLightLevel,
                                                 int startSkyBrightness, int endSkyBrightness,
                                                 float r, float g, float b, boolean tiltType) {
        for (int i = 0; i < 2; ++i) {
            float phase = (float)(currentCount + i) / (float)maxCount;
            int lightLevel = LightCoordsUtil.pack(
                    (int)Mth.lerp(phase, (float)startBlockLightLevel, (float)endBlockLightLevel),
                    (int)Mth.lerp(phase, (float)startSkyBrightness, (float)endSkyBrightness));
            Vec3 midPointD = zipline.getMidPointOffsetFromStart(phase);
            Vector3f midPoint = new Vector3f((float)midPointD.x(), (float)midPointD.y(), (float)midPointD.z());
            float tilt = zipline.getSlope(phase);
            float tiltInv = Mth.invSqrt(tilt * tilt + 1.0F);
            float yOffset = 0.075F * tiltInv / 1.4142135F;
            float xBaseOffset = unitLengthX * 0.075F * tilt * tiltInv / 1.4142135F;
            float zBaseOffset = unitLengthZ * 0.075F * tilt * tiltInv / 1.4142135F;
            float sign = tiltType ? 1.0F : -1.0F;
            float xOffset = sign * unitLengthZ * 0.075F / 1.4142135F;
            float zOffset = sign * -unitLengthX * 0.075F / 1.4142135F;

            if (i == 0) {
                vertexConsumer.addVertex(transformMatrix, midPoint.x() + xBaseOffset + xOffset,
                                midPoint.y() - yOffset, midPoint.z() + zBaseOffset + zOffset)
                        .setColor(r, g, b, 1.0F).setLight(lightLevel);
                vertexConsumer.addVertex(transformMatrix, midPoint.x() - xBaseOffset - xOffset,
                                midPoint.y() + yOffset, midPoint.z() - zBaseOffset - zOffset)
                        .setColor(r, g, b, 1.0F).setLight(lightLevel);
            } else {
                vertexConsumer.addVertex(transformMatrix, midPoint.x() - xBaseOffset - xOffset,
                                midPoint.y() + yOffset, midPoint.z() - zBaseOffset - zOffset)
                        .setColor(r, g, b, 1.0F).setLight(lightLevel);
                vertexConsumer.addVertex(transformMatrix, midPoint.x() + xBaseOffset + xOffset,
                                midPoint.y() - yOffset, midPoint.z() + zBaseOffset + zOffset)
                        .setColor(r, g, b, 1.0F).setLight(lightLevel);
            }
        }
    }

    private static void renderRopeSingleBlock3D(Matrix4fc transformMatrix, VertexConsumer vertexConsumer,
                                                 Zipline zipline, int currentCount, int maxCount,
                                                 float unitLengthX, float unitLengthZ,
                                                 int startBlockLightLevel, int endBlockLightLevel,
                                                 int startSkyBrightness, int endSkyBrightness,
                                                 float r, float g, float b) {
        Vector3f[] vertexList = new Vector3f[8];
        int[] lightLevelList = new int[2];
        for (int i = 0; i < 2; ++i) {
            float phase = (float)(currentCount + i) / (float)maxCount;
            lightLevelList[i] = LightCoordsUtil.pack(
                    (int)Mth.lerp(phase, (float)startBlockLightLevel, (float)endBlockLightLevel),
                    (int)Mth.lerp(phase, (float)startSkyBrightness, (float)endSkyBrightness));
            Vec3 midPointD = zipline.getMidPointOffsetFromStart(phase);
            Vector3f midPoint = new Vector3f((float)midPointD.x(), (float)midPointD.y(), (float)midPointD.z());
            float tilt = zipline.getSlope(phase);
            float tiltInv = Mth.invSqrt(tilt * tilt + 1.0F);
            float yOffset = 0.075F * tiltInv / 1.4142135F;
            float xBaseOffset = unitLengthX * 0.075F * tilt * tiltInv / 1.4142135F;
            float zBaseOffset = unitLengthZ * 0.075F * tilt * tiltInv / 1.4142135F;
            float xOffset = unitLengthZ * 0.075F / 1.4142135F;
            float zOffset = -unitLengthX * 0.075F / 1.4142135F;
            vertexList[4 * i] = new Vector3f(midPoint.x() - xBaseOffset + xOffset,
                    midPoint.y() + yOffset, midPoint.z() - zBaseOffset + zOffset);
            vertexList[4 * i + 1] = new Vector3f(midPoint.x() - xBaseOffset - xOffset,
                    midPoint.y() + yOffset, midPoint.z() - zBaseOffset - zOffset);
            vertexList[4 * i + 2] = new Vector3f(midPoint.x() + xBaseOffset - xOffset,
                    midPoint.y() - yOffset, midPoint.z() + zBaseOffset - zOffset);
            vertexList[4 * i + 3] = new Vector3f(midPoint.x() + xBaseOffset + xOffset,
                    midPoint.y() - yOffset, midPoint.z() + zBaseOffset + zOffset);
        }

        for (int i = 0; i < 4; ++i) {
            addVertex(transformMatrix, vertexConsumer, vertexList[i], r, g, b, lightLevelList[0]);
            addVertex(transformMatrix, vertexConsumer, vertexList[(i + 1) % 4], r, g, b, lightLevelList[0]);
            addVertex(transformMatrix, vertexConsumer, vertexList[4 + (i + 1) % 4], r, g, b, lightLevelList[1]);
            addVertex(transformMatrix, vertexConsumer, vertexList[4 + i], r, g, b, lightLevelList[1]);
        }
        if (currentCount == 0) {
            addVertex(transformMatrix, vertexConsumer, vertexList[3], r, g, b, lightLevelList[0]);
            addVertex(transformMatrix, vertexConsumer, vertexList[2], r, g, b, lightLevelList[0]);
            addVertex(transformMatrix, vertexConsumer, vertexList[1], r, g, b, lightLevelList[0]);
            addVertex(transformMatrix, vertexConsumer, vertexList[0], r, g, b, lightLevelList[0]);
        } else if (currentCount == maxCount - 1) {
            addVertex(transformMatrix, vertexConsumer, vertexList[4], r, g, b, lightLevelList[1]);
            addVertex(transformMatrix, vertexConsumer, vertexList[5], r, g, b, lightLevelList[1]);
            addVertex(transformMatrix, vertexConsumer, vertexList[6], r, g, b, lightLevelList[1]);
            addVertex(transformMatrix, vertexConsumer, vertexList[7], r, g, b, lightLevelList[1]);
        }
    }

    private static void addVertex(Matrix4fc matrix, VertexConsumer consumer, Vector3f pos,
                                  float r, float g, float b, int light) {
        consumer.addVertex(matrix, pos.x(), pos.y(), pos.z())
                .setColor(r, g, b, 1.0F)
                .setLight(light);
    }

    public static class ZiplineRopeRenderState extends EntityRenderState {
        public boolean valid;
        public Zipline zipline;
        public Vec3 startOffset = Vec3.ZERO;
        public boolean render3d;
        public float red;
        public float green;
        public float blue;
        public int startBlockLight;
        public int endBlockLight;
        public int startSkyLight;
        public int endSkyLight;
    }
}
