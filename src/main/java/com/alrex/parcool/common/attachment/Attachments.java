package com.alrex.parcool.common.attachment;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.platform.attachment.AttachmentType;
import com.alrex.parcool.platform.eventbus.IEventBus;

import java.util.function.Supplier;

public class Attachments {
    private static final AttachmentType<ReadonlyStamina> STAMINA_TYPE =
            AttachmentType.persistent("stamina", ReadonlyStamina::createDefault, ReadonlyStamina.CODEC);
    private static final AttachmentType<Parkourability> PARKOURABILITY_TYPE =
            AttachmentType.of("parkourability", Parkourability::new);

    public static final Supplier<AttachmentType<ReadonlyStamina>> STAMINA = () -> STAMINA_TYPE;
    public static final Supplier<AttachmentType<Parkourability>> PARKOURABILITY = () -> PARKOURABILITY_TYPE;

    public static AttachmentType<ReadonlyStamina> staminaType() { return STAMINA_TYPE; }

    public static void registerAll(IEventBus bus) {
        // nothing to register - attachments are plain objects on Fabric
    }
}
