package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.world.entity.player.Player;

public class CrawlAnimator
extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(Crawl.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float factor = (float)Math.sin((double)(transformer.getLimbSwing() / 10.0f) * Math.PI);
        float leftZFactor = (float)Math.cos((double)(transformer.getLimbSwing() / 10.0f) * Math.PI);
        float rightZFactor = -leftZFactor;
        if (leftZFactor < 0.0f) {
            leftZFactor /= 5.0f;
        }
        if (rightZFactor < 0.0f) {
            rightZFactor /= 5.0f;
        }
        transformer.translateLeftLeg(0.0f, -0.1f + 0.8f * factor, -0.6f + 0.6f * factor).translateRightLeg(0.0f, -0.1f - 0.8f * factor, -0.6f - 0.6f * factor).rotateLeftArm((float)Math.toRadians(-15.0f + 45.0f * leftZFactor), 0.0f, (float)Math.toRadians(-130.0f - 30.0f * factor)).rotateRightArm((float)Math.toRadians(-15.0f + 45.0f * rightZFactor), 0.0f, (float)Math.toRadians(130.0f - 30.0f * factor)).rotateLeftLeg((float)Math.toRadians(-11.0f + 7.0f * factor), 0.0f, (float)Math.toRadians(-5.0f + 5.0f * factor)).rotateRightLeg((float)Math.toRadians(-11.0f - 7.0f * factor), 0.0f, (float)Math.toRadians(5.0f + 5.0f * factor)).end();
    }
}

