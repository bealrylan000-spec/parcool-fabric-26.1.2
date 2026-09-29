package com.alrex.parcool.common.action;

import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.world.entity.player.Player;

public class AdditionalProperties {
    private int sprintingTick = 0;
    private int notLandingTick = 0;
    private int previousNotLandingTick = Integer.MAX_VALUE;
    private int landingTick = 0;
    private int lastSprintingTick = 0;
    private int notSprintingTick = 0;
    private int notCreativeFlyingTick = 0;
    private int sneakingTick = 0;
    private int notSneakingTick = 0;
    private int lastSneakingTick = 0;
    private int inWaterTick = 0;
    private int notInWaterTick = 0;
    private int tickAfterLastJump = 0;
    private int actualNotLandingTick = 0;
    private boolean inAirByJumping = false;

    public void onJump() {
        this.tickAfterLastJump = 0;
        this.inAirByJumping = true;
    }

    public void onTick(Player player, Parkourability parkourability) {
        if (player.onGround) {
            this.inAirByJumping = false;
            this.actualNotLandingTick = 0;
        } else {
            ++this.actualNotLandingTick;
        }
        ++this.tickAfterLastJump;
        if (player.isSprinting()) {
            this.notSprintingTick = 0;
            ++this.sprintingTick;
            this.lastSprintingTick = this.sprintingTick;
        } else {
            this.sprintingTick = 0;
            ++this.notSprintingTick;
        }
        if (player.isShiftKeyDown()) {
            ++this.sneakingTick;
            this.notSneakingTick = 0;
            this.lastSneakingTick = this.sneakingTick;
        } else {
            this.sneakingTick = 0;
            ++this.notSneakingTick;
        }
        if (player.onGround()) {
            if (this.notLandingTick > 0) {
                this.previousNotLandingTick = this.notLandingTick;
            }
            this.notLandingTick = 0;
            ++this.landingTick;
        } else {
            ++this.notLandingTick;
            this.landingTick = 0;
        }
        this.notCreativeFlyingTick = player.getAbilities().flying ? 0 : ++this.notCreativeFlyingTick;
        if (player.isInWaterOrBubble()) {
            ++this.inWaterTick;
            this.notInWaterTick = 0;
        } else {
            this.inWaterTick = 0;
            ++this.notInWaterTick;
        }
    }

    public int getSprintingTick() {
        return this.sprintingTick;
    }

    public int getNotLandingTick() {
        return this.notLandingTick;
    }

    public int getPreviousNotLandingTick() {
        return this.previousNotLandingTick;
    }

    public int getLastSprintingTick() {
        return this.lastSprintingTick;
    }

    public int getLandingTick() {
        return this.landingTick;
    }

    public int getNotSprintingTick() {
        return this.notSprintingTick;
    }

    public int getNotCreativeFlyingTick() {
        return this.notCreativeFlyingTick;
    }

    public int getInWaterTick() {
        return this.inWaterTick;
    }

    public int getNotInWaterTick() {
        return this.notInWaterTick;
    }

    public int getTickAfterLastJump() {
        return this.tickAfterLastJump;
    }

    public int getSneakingTick() {
        return this.sneakingTick;
    }

    public int getNotSneakingTick() {
        return this.notSneakingTick;
    }

    public int getLastSneakingTick() {
        return this.lastSneakingTick;
    }

    public int getActualNotLandingTick() {
        return this.actualNotLandingTick;
    }

    public boolean isInAirByJumping() {
        return this.inAirByJumping;
    }
}

