# ParCool Fabric 26.1.2 compatibility fixes

This update addresses the first GitHub Actions compiler failures and several immediately-adjacent 26.1 migration issues.

Key changes:
- Allows typed config values to extend `ConfigValue`.
- Adds compile-time `javax.annotation` support through JSR-305.
- Fixes the internal `ClientTickEvent` package reference.
- Updates HUD registration/rendering to Fabric/Minecraft 26.1 APIs (`HudElementRegistry`, `HudElement`, `GuiGraphicsExtractor`).
- Migrates remaining `ResourceLocation` references in the affected port code to `Identifier`.
- Updates several renamed 26.1 client/key/render/network symbols.
- Disables hard compile-time links to optional integrations whose pre-26.1 APIs cannot be used on 26.1.2.
- Replaces the legacy settings screens with a small 26.1-compatible placeholder screen so the old immediate-mode GUI does not block compilation.

The rich settings GUI and optional mod integrations can be restored separately after the core 26.1.2 port builds and launches.
