package com.alrex.parcool.platform.attachment;

import java.util.Map;

/** Implemented onto Entity by EntityAttachmentMixin; holds per-entity attachment values. */
public interface AttachmentStore {
    Map<AttachmentType<?>, Object> parcool$attachments();
}
