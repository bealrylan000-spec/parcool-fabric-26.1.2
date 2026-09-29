package com.alrex.parcool.common.attachment;

import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.platform.attachment.AttachmentType;
import com.alrex.parcool.platform.eventbus.IEventBus;

import java.util.function.Supplier;

public class ClientAttachments {
    private static final AttachmentType<LocalStamina> LOCAL_STAMINA_TYPE = AttachmentType.of("local_stamina", LocalStamina::new);
    private static final AttachmentType<Animation> ANIMATION_TYPE = AttachmentType.of("animation", Animation::new);
    public static final Supplier<AttachmentType<LocalStamina>> LOCAL_STAMINA = () -> LOCAL_STAMINA_TYPE;
    public static final Supplier<AttachmentType<Animation>> ANIMATION = () -> ANIMATION_TYPE;

    public static void registerAll(IEventBus bus) {
    }
}
