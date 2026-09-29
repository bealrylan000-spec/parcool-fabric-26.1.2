package com.alrex.parcool.extern.shouldersurfing;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.extern.ModManager;

/** Optional Shoulder Surfing integration is disabled in the standalone 26.1.2 build. */
public class ShoulderSurfingManager extends ModManager {
    public ShoulderSurfingManager() { super("shouldersurfing"); }
    public Dodge.DodgeDirection handleCustomCameraRotationForDodge(Dodge.DodgeDirection direction) { return direction; }
    public Boolean isCameraDecoupled() { return false; }
}
