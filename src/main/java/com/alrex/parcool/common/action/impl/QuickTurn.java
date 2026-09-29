package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;
import java.nio.ByteBuffer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;

public class QuickTurn
extends Action {
    private static final int AnimationTickLength = 4;
    private boolean turnRightward = false;
    private Vec3 startAngle = null;

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        Vec3 angle = player.getLookAngle();
        startInfo.putDouble(angle.x).putDouble(angle.z);
        return KeyRecorder.keyQuickTurn.isPressed() && !parkourability.get(Vault.class).isDoing() && !parkourability.get(Roll.class).isDoing() && !parkourability.get(Flipping.class).isDoing() && !parkourability.get(ClingToCliff.class).isDoing();
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return this.getDoingTick() < 4;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.turnRightward = !this.turnRightward;
        this.startAngle = new Vec3(startData.getDouble(), 0.0, startData.getDouble()).normalize();
    }

    @Override
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
        if (this.isDoing() && this.startAngle != null) {
            float renderTick = (float)this.getDoingTick() + event.getPartialTick().getGameTimeDeltaPartialTick(true);
            float animationPhase = renderTick / 4.0f;
            Vec3 rotatedAngle = this.startAngle.yRot((float)(Math.PI * (double)animationPhase * (double)(this.turnRightward ? -1 : 1)));
            player.setYRot((float)VectorUtil.toYawDegree(rotatedAngle));
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }
}

