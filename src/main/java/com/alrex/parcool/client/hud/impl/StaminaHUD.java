package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.event.tick.ClientTickEvent;

@OnlyIn(value=Dist.CLIENT)
public class StaminaHUD {
    public static final Identifier STAMINA = Identifier.fromNamespaceAndPath((String)"parcool", (String)"textures/gui/stamina_bar.png");
    private float shadowScale = 1.0f;
    private int renderGageType = 0;
    private int renderGageTick = 0;
    private float statusValue = 0.0f;
    private float oldStatusValue = 0.0f;
    private boolean showStatus = false;

    public void onTick(ClientTickEvent.Post event, LocalPlayer player) {
        Parkourability parkourability = Parkourability.get((Player)player);
        if (parkourability == null) {
            return;
        }
        if (++this.renderGageTick >= 5) {
            this.renderGageTick = 0;
            if (++this.renderGageType > 2) {
                this.renderGageType = 0;
            }
        }
        this.oldStatusValue = this.statusValue;
        boolean oldShowStatus = this.showStatus;
        this.showStatus = false;
        for (Action a : parkourability.getList()) {
            if (!a.wantsToShowStatusBar(player, parkourability)) continue;
            this.showStatus = true;
            this.statusValue = a.getStatusValue(player, parkourability);
            if (this.statusValue > 1.0f) {
                this.statusValue = 1.0f;
                break;
            }
            if (!(this.statusValue < 0.0f)) break;
            this.statusValue = 0.0f;
            break;
        }
        if (!oldShowStatus && this.showStatus) {
            this.oldStatusValue = this.statusValue;
        }
    }

    public void render(GuiGraphicsExtractor graphics, Parkourability parkourability, ReadonlyStamina stamina, float partialTick) {
        float statusScale;
        Position position = new Position((Position.Horizontal)((Object)ParCoolConfig.Client.getInstance().AlignHorizontalStaminaHUD.get()), (Position.Vertical)((Object)ParCoolConfig.Client.getInstance().AlignVerticalStaminaHUD.get()), ParCoolConfig.Client.Integers.HorizontalOffsetOfStaminaHUD.get(), ParCoolConfig.Client.Integers.VerticalOffsetOfStaminaHUD.get());
        int boxWidth = 91;
        int boxHeight = 17;
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        Tuple<Integer, Integer> pos = position.calculate(91, 17, width, height);
        float staminaScale = (float)stamina.value() / (float)stamina.max();
        float f = statusScale = this.showStatus ? MathUtil.lerp(this.oldStatusValue, this.statusValue, partialTick) : 0.0f;
        if (staminaScale < 0.0f) {
            staminaScale = 0.0f;
        }
        if (staminaScale > 1.0f) {
            staminaScale = 1.0f;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, STAMINA, ((Integer)pos.getA()).intValue(), ((Integer)pos.getB()).intValue(), 0, 0, 93, 17, 128, 128);
        if (!stamina.isExhausted()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, STAMINA, ((Integer)pos.getA()).intValue(), ((Integer)pos.getB()).intValue(), 0, 102, (int)Math.ceil(92.0f * statusScale), 17, 128, 128);
            graphics.blit(RenderPipelines.GUI_TEXTURED, STAMINA, ((Integer)pos.getA()).intValue(), ((Integer)pos.getB()).intValue(), 0, 85, Math.round(16.0f + 69.0f * this.shadowScale) + 1, 12, 128, 128);
            graphics.blit(RenderPipelines.GUI_TEXTURED, STAMINA, ((Integer)pos.getA()).intValue(), ((Integer)pos.getB()).intValue(), 0.0f, (17 * (this.renderGageType + 1)), Math.round(16.0f + 69.0f * staminaScale) + 1, 12, 128, 128);
        } else {
            graphics.blit(RenderPipelines.GUI_TEXTURED, STAMINA, ((Integer)pos.getA()).intValue(), ((Integer)pos.getB()).intValue(), 0, 68, Math.round(16.0f + 69.0f * staminaScale) + 1, 17, 128, 128);
        }
        this.shadowScale = staminaScale - (staminaScale - this.shadowScale) / 1.1f;
    }
}

