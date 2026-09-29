package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;
import com.alrex.parcool.platform.event.client.ViewportEvent;

public abstract class Animator {
    private int tick = 0;

    public void tick(Player player) {
        ++this.tick;
    }

    protected int getTick() {
        return this.tick;
    }

    public abstract boolean shouldRemoved(Player var1, Parkourability var2);

    public boolean animatePre(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        return false;
    }

    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
    }

    public boolean rotatePre(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        return false;
    }

    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
    }

    public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
    }

    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
    }
}

