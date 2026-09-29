package com.alrex.parcool.extern.betterthirdperson;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.extern.ModManager;

/** Optional Better Third Person integration is disabled until a 26.1.2 API is available. */
public class BetterThirdPersonManager extends ModManager {
    public BetterThirdPersonManager() { super("betterthirdperson"); }
    public Dodge.DodgeDirection handleCustomCameraRotationForDodge(Dodge.DodgeDirection direction) { return direction; }
    public boolean isCameraDecoupled() { return false; }
}
