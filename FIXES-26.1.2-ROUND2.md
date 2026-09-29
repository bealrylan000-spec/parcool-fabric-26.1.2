# Minecraft 26.1.2 compatibility fixes - round 2

This update addresses the second GitHub Actions compiler batch from commit
`7768be9a19bde513a637b88699174ec2ac7f3994`.

## Fixed

- Removed the `Limitation` simple-name collision between the public API wrapper
  and the server limitation implementation.
- Updated `PlayerModel` imports to Minecraft 26.1.2's
  `net.minecraft.client.model.player.PlayerModel` package.
- Updated animation partial-tick access from the removed Minecraft timer getter
  to `Minecraft#getDeltaTracker()`.
- Replaced removed `LightTexture.pack` usage with `LightCoordsUtil.pack`.
- Ported the zipline rope entity renderer to Minecraft 26.1's render-state
  extract/submit pipeline and `SubmitNodeCollector` custom geometry path.
- Replaced the legacy custom RenderType builder with a vanilla 26.1-compatible
  leash render type as a temporary compile-first rope pipeline.
- Updated `LivingRendererMixin` for the two-generic entity renderer era and the
  current `shouldShowName(LivingEntity,double)` method.
- Replaced the old player renderer/model animation mixins with safe compatibility
  placeholders. The legacy animation hooks accepted entity-driven arguments,
  while Minecraft 26.1 player models now animate from `AvatarRenderState`.

## Temporary behavior differences

- Core parkour/gameplay code remains present.
- ParCool's custom third-person player pose animations are temporarily disabled.
- Zipline ropes use the vanilla leash render type as a compatibility fallback;
  exact legacy rope appearance can be restored after the build/runtime port is stable.

GitHub Actions is still the authoritative compile test because this prepared
project requires Minecraft 26.1.2 dependencies and Java 25.
