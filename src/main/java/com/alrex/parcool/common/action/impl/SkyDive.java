package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import java.nio.ByteBuffer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;

public class SkyDive
extends Action {
    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        return parkourability.get(Dive.class).getDoingTick() > 15 && this.getNotDoingTick() > 20 && KeyRecorder.keyJumpState.isPressed();
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return parkourability.get(Dive.class).isDoing() && !KeyRecorder.keyJumpState.isPressed();
    }

    @Override
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        LocalPlayer clientPlayer = (LocalPlayer)player;
        Vec3 forwardVec = VectorUtil.fromYawDegree(player.yHeadRot);
        Vec3 leftVec = forwardVec.yRot(1.5707964f).scale((double)clientPlayer.input.leftImpulse * 0.0);
        forwardVec = forwardVec.scale((double)clientPlayer.input.forwardImpulse * 0.03);
        clientPlayer.setDeltaMovement(clientPlayer.getDeltaMovement().multiply(1.0, parkourability.getLimitedValue(ParCoolConfig.Client.Doubles.SkyDiveSpeedDecreaseRate, ParCoolConfig.Server.Doubles.MinSkyDiveSpeedDecreaseRate), 1.0).add(forwardVec.add(leftVec)));
    }

    @Override
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
        if (this.isDoing()) {
            player.setYBodyRot(player.yHeadRot);
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }
}

