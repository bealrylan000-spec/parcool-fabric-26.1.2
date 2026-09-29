package com.alrex.parcool.server.limitation;

public class ActionLimitation {
    private final boolean possible;
    private final int staminaConsumption;

    public boolean isPossible() {
        return this.possible;
    }

    public int getLeastStaminaConsumption() {
        return this.staminaConsumption;
    }

    public ActionLimitation(boolean possible, int leastStaminaConsumption) {
        this.possible = possible;
        this.staminaConsumption = leastStaminaConsumption;
    }
}

