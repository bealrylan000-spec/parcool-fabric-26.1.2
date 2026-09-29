package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.RenderBehaviorEnforcer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Options.class})
public abstract class OptionsMixin {
    @Inject(method={"getCameraType"}, at={@At(value="HEAD")}, cancellable=true)
    public void onGetCameraType(CallbackInfoReturnable<CameraType> cir) {
        CameraType cameraType = RenderBehaviorEnforcer.getEnforcedCameraType();
        if (cameraType != null) {
            cir.setReturnValue((Object)cameraType);
        }
    }
}

