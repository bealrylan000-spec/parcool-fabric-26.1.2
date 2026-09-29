package com.alrex.parcool.common.action;

import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.common.Parkourability;
import java.nio.ByteBuffer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;

public abstract class Action {
    private boolean doing = false;
    private int doingTick = 0;
    private int notDoingTick = 0;
    private int tickFromStarted = -1;

    public boolean isJustStarted() {
        return this.isDoing() && this.getDoingTick() == 0;
    }

    public int getDoingTick() {
        return this.doingTick;
    }

    public int getNotDoingTick() {
        return this.notDoingTick;
    }

    public int getTickFromLastStarted() {
        return this.tickFromStarted;
    }

    public boolean isDoing() {
        return this.doing;
    }

    public void tick() {
        if (this.doing) {
            ++this.doingTick;
            this.notDoingTick = 0;
        } else {
            ++this.notDoingTick;
            this.doingTick = 0;
        }
        if (this.tickFromStarted >= 0) {
            ++this.tickFromStarted;
        }
    }

    public void start(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        this.doing = true;
        this.tickFromStarted = 0;
        this.onStart(player, parkourability, startInfo);
        startInfo.rewind();
        if (player.isLocalPlayer()) {
            this.onStartInLocalClient(player, parkourability, startInfo);
        } else if (player.level().isClientSide()) {
            this.onStartInOtherClient(player, parkourability, startInfo);
        } else {
            this.onStartInServer(player, parkourability, startInfo);
        }
        startInfo.rewind();
    }

    public void finish(Player player) {
        this.doing = false;
        if (player.isLocalPlayer()) {
            this.onStopInLocalClient(player);
        } else if (player.level().isClientSide()) {
            this.onStopInOtherClient(player);
        } else {
            this.onStopInServer(player);
        }
        this.onStop(player);
    }

    @OnlyIn(value=Dist.CLIENT)
    public abstract boolean canStart(Player var1, Parkourability var2, ByteBuffer var3);

    @OnlyIn(value=Dist.CLIENT)
    public abstract boolean canContinue(Player var1, Parkourability var2);

    public void onStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
    }

    public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startInfo) {
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startInfo) {
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startInfo) {
    }

    public void onStop(Player player) {
    }

    public void onStopInServer(Player player) {
    }

    public void onStopInOtherClient(Player player) {
    }

    public void onStopInLocalClient(Player player) {
    }

    public void onWorkingTick(Player player, Parkourability parkourability) {
    }

    public void onWorkingTickInServer(Player player, Parkourability parkourability) {
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onWorkingTickInOtherClient(Player player, Parkourability parkourability) {
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
    }

    public void onTick(Player player, Parkourability parkourability) {
    }

    public void onServerTick(Player player, Parkourability parkourability) {
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onClientTick(Player player, Parkourability parkourability) {
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
    }

    public void restoreSynchronizedState(ByteBuffer buffer) {
    }

    public void saveSynchronizedState(ByteBuffer buffer) {
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean wantsToShowStatusBar(LocalPlayer player, Parkourability parkourability) {
        return false;
    }

    @OnlyIn(value=Dist.CLIENT)
    public float getStatusValue(LocalPlayer player, Parkourability parkourability) {
        return 0.0f;
    }

    public abstract StaminaConsumeTiming getStaminaConsumeTiming();
}

