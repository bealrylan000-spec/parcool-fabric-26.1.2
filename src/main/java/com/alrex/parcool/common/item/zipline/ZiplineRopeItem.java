package com.alrex.parcool.common.item.zipline;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.common.block.zipline.ZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.block.zipline.ZiplineInfo;
import com.alrex.parcool.common.item.DataComponents;
import com.alrex.parcool.common.item.component.ZiplineColorComponent;
import com.alrex.parcool.common.item.component.ZiplinePositionComponent;
import com.alrex.parcool.common.item.component.ZiplineTensionComponent;
import com.alrex.parcool.common.zipline.ZiplineType;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ZiplineRopeItem
extends Item {
    public static final int DEFAULT_COLOR = -11763738;
    private static final DecimalFormat PERCENT_FORMATTER;

    public ZiplineRopeItem(Item.Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public void appendHoverText(ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        ZiplinePositionComponent posComponent = (ZiplinePositionComponent)stack.getComponents().get(DataComponents.ZIPLINE_POSITION.get());
        if (posComponent != null) {
            lines.add((Component)Component.translatable((String)"parcool.gui.text.zipline.bind_pos", (Object[])new Object[]{posComponent.pos().getX() + ", " + posComponent.pos().getY() + ", " + posComponent.pos().getZ()}).withStyle(ChatFormatting.YELLOW));
        } else {
            lines.add((Component)Component.translatable((String)"parcool.gui.text.zipline.not_bound").withStyle(ChatFormatting.DARK_GRAY));
        }
        lines.add((Component)Component.empty());
        lines.add((Component)Component.translatable((String)"parcool.gui.text.zipline.tension", (Object[])new Object[]{ZiplineRopeItem.getZiplineType(stack).getTranslationName()}).withStyle(ChatFormatting.GRAY));
        if (ZiplineRopeItem.hasCustomColor(stack)) {
            lines.add((Component)Component.translatable((String)"parcool.gui.text.zipline.colored").withStyle(ChatFormatting.BLUE));
        }
    }

    @Nonnull
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (ZiplineRopeItem.hasBlockPosition(stack)) {
            if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof ZiplineHookBlock) {
                Player player;
                BlockPos start = ZiplineRopeItem.getBlockPosition(stack);
                if (start == null) {
                    return InteractionResult.FAIL;
                }
                BlockPos end = context.getClickedPos();
                if (start.equals((Object)end)) {
                    return InteractionResult.PASS;
                }
                double horizontalDistSqr = Mth.square((int)(start.getX() - end.getX())) + Mth.square((int)(start.getZ() - end.getZ()));
                if (horizontalDistSqr > 5184.0) {
                    Player player2;
                    if (context.getLevel().isClientSide() && (player2 = context.getPlayer()) != null) {
                        player2.sendSystemMessage((Component)Component.translatable((String)"parcool.message.zipline.too_far"));
                    }
                    return InteractionResult.FAIL;
                }
                double verticalDist = Math.abs(end.getY() - start.getY());
                if (verticalDist * Mth.fastInvSqrt((double)horizontalDistSqr) > 1.0 || verticalDist > 36.72) {
                    Player player3;
                    if (context.getLevel().isClientSide() && (player3 = context.getPlayer()) != null) {
                        player3.sendSystemMessage((Component)Component.translatable((String)"parcool.message.zipline.too_steep"));
                    }
                    return InteractionResult.FAIL;
                }
                BlockEntity startEntity = context.getLevel().getBlockEntity(start);
                BlockEntity endEntity = context.getLevel().getBlockEntity(end);
                if (startEntity instanceof ZiplineHookTileEntity) {
                    ZiplineHookTileEntity startZipEntity = (ZiplineHookTileEntity)startEntity;
                    if (endEntity instanceof ZiplineHookTileEntity) {
                        Player player4;
                        ZiplineHookTileEntity endZipEntity = (ZiplineHookTileEntity)endEntity;
                        if (ZiplineRopeItem.getZiplineType(stack).getZipline(startZipEntity.getActualZiplinePoint(null), endZipEntity.getActualZiplinePoint(null)).conflictsWithSomething(context.getLevel())) {
                            Player player5 = context.getPlayer();
                            if (player5 != null) {
                                player5.sendSystemMessage((Component)Component.translatable((String)"parcool.message.zipline.obstacle_detected"));
                            }
                            return InteractionResult.FAIL;
                        }
                        if (!context.getLevel().isClientSide()) {
                            if (!startZipEntity.connectTo(endZipEntity, new ZiplineInfo(ZiplineRopeItem.getZiplineType(stack), ZiplineRopeItem.getColor(stack)))) {
                                Player player6 = context.getPlayer();
                                if (player6 != null) {
                                    player6.sendSystemMessage((Component)Component.translatable((String)"parcool.message.zipline.already_exist"));
                                }
                                return InteractionResult.FAIL;
                            }
                            stack.shrink(1);
                        }
                        if ((player4 = context.getPlayer()) != null) {
                            player4.playSound((SoundEvent)SoundEvents.ZIPLINE_SET.get(), 1.0f, 1.0f);
                        }
                        ZiplineRopeItem.removeBlockPosition(stack);
                        return InteractionResult.sidedSuccess((boolean)context.getLevel().isClientSide());
                    }
                }
                ZiplineRopeItem.removeBlockPosition(stack);
                if (context.getLevel().isClientSide() && (player = context.getPlayer()) != null) {
                    player.sendSystemMessage((Component)Component.translatable((String)"parcool.message.zipline.point_not_found"));
                }
                return InteractionResult.FAIL;
            }
            if (context.isSecondaryUseActive()) {
                Player player;
                if (context.getLevel().isClientSide() && (player = context.getPlayer()) != null) {
                    player.sendSystemMessage((Component)Component.translatable((String)"parcool.message.zipline.reset_point"));
                }
                ZiplineRopeItem.removeBlockPosition(stack);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        BlockPos pos = context.getClickedPos();
        if (context.getLevel().getBlockState(pos).getBlock() instanceof ZiplineHookBlock) {
            Player player;
            ZiplineRopeItem.setBlockPosition(stack, pos);
            if (context.getLevel().isClientSide() && (player = context.getPlayer()) != null) {
                player.sendSystemMessage((Component)Component.translatable((String)"parcool.message.zipline.set_point", (Object[])new Object[]{pos.toShortString()}));
            }
            return InteractionResult.SUCCESS;
        }
        if (context.isSecondaryUseActive()) {
            Player player;
            ZiplineRopeItem.changeZiplineType(stack);
            if (context.getLevel().isClientSide() && (player = context.getPlayer()) != null) {
                player.sendSystemMessage((Component)Component.translatable((String)"parcool.message.zipline.change_tension", (Object[])new Object[]{ZiplineRopeItem.getZiplineType(stack).getTranslationName()}));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static void setBlockPosition(ItemStack stack, BlockPos pos) {
        stack.set(DataComponents.ZIPLINE_POSITION, (Object)new ZiplinePositionComponent(pos));
    }

    public static void removeBlockPosition(ItemStack stack) {
        stack.remove(DataComponents.ZIPLINE_POSITION);
    }

    public static boolean hasBlockPosition(ItemStack stack) {
        return stack.has(DataComponents.ZIPLINE_POSITION.get());
    }

    @Nullable
    public static BlockPos getBlockPosition(ItemStack stack) {
        ZiplinePositionComponent posComp = (ZiplinePositionComponent)stack.getComponents().get(DataComponents.ZIPLINE_POSITION.get());
        return posComp == null ? null : posComp.pos();
    }

    public static void setColor(ItemStack stack, int color) {
        if (color != -11763738) {
            stack.set(DataComponents.ZIPLINE_COLOR, (Object)new ZiplineColorComponent(color));
        } else {
            stack.remove(DataComponents.ZIPLINE_COLOR);
        }
    }

    public static int getColor(ItemStack stack) {
        ZiplineColorComponent colorComp = (ZiplineColorComponent)stack.get(DataComponents.ZIPLINE_COLOR);
        return colorComp == null ? -11763738 : colorComp.color();
    }

    public static boolean hasCustomColor(ItemStack stack) {
        ZiplineColorComponent colorComp = (ZiplineColorComponent)stack.get(DataComponents.ZIPLINE_COLOR);
        return colorComp != null && colorComp.color() != -11763738;
    }

    public static ZiplineType getZiplineType(ItemStack stack) {
        ZiplineTensionComponent tensionComp = (ZiplineTensionComponent)stack.get(DataComponents.ZIPLINE_TENSION);
        return tensionComp == null ? ZiplineType.STANDARD : tensionComp.type();
    }

    public static void changeZiplineType(ItemStack stack) {
        ZiplineType currentType = ZiplineRopeItem.getZiplineType(stack);
        ZiplineRopeItem.setZiplineType(stack, ZiplineType.values()[(ZiplineRopeItem.getZiplineType(stack).ordinal() + 1) % ZiplineType.values().length]);
    }

    public static void setZiplineType(ItemStack stack, ZiplineType type) {
        stack.set(DataComponents.ZIPLINE_TENSION, (Object)new ZiplineTensionComponent(type));
    }

    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(' ');
        PERCENT_FORMATTER = new DecimalFormat("##0.0", decimalFormatSymbols);
    }

    public static class RopeColor
    implements ItemColor {
        public int getColor(@Nonnull ItemStack itemStack, int i) {
            return i > 0 ? -1 : ZiplineRopeItem.getColor(itemStack);
        }
    }
}

