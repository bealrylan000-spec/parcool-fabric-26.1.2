package com.alrex.parcool.common.action;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.impl.BreakfallReady;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.ClimbPoles;
import com.alrex.parcool.common.action.impl.ClimbUp;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.FastSwim;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.action.impl.HorizontalWallRun;
import com.alrex.parcool.common.action.impl.JumpFromBar;
import com.alrex.parcool.common.action.impl.QuickTurn;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.action.impl.SkyDive;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.action.impl.VerticalWallRun;
import com.alrex.parcool.common.action.impl.WallJump;
import com.alrex.parcool.common.action.impl.WallSlide;
import com.alrex.parcool.common.action.instant.StartSwimByCrawl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class Actions {
    public static final List<ActionRegistry<? extends Action>> ACTION_REGISTRIES = Arrays.asList(new ActionRegistry<BreakfallReady>(BreakfallReady.class, BreakfallReady::new, 0), new ActionRegistry<CatLeap>(CatLeap.class, CatLeap::new, 200), new ActionRegistry<ChargeJump>(ChargeJump.class, ChargeJump::new, 100), new ActionRegistry<ClimbPoles>(ClimbPoles.class, ClimbPoles::new, 0), new ActionRegistry<ClimbUp>(ClimbUp.class, ClimbUp::new, 150), new ActionRegistry<ClingToCliff>(ClingToCliff.class, ClingToCliff::new, 2), new ActionRegistry<HideInBlock>(HideInBlock.class, HideInBlock::new, 0), new ActionRegistry<Crawl>(Crawl.class, Crawl::new, 0), new ActionRegistry<Dive>(Dive.class, Dive::new, 0), new ActionRegistry<Dodge>(Dodge.class, Dodge::new, 80), new ActionRegistry<FastRun>(FastRun.class, FastRun::new, 2), new ActionRegistry<FastSwim>(FastSwim.class, FastSwim::new, 3), new ActionRegistry<Flipping>(Flipping.class, Flipping::new, 80), new ActionRegistry<HangDown>(HangDown.class, HangDown::new, 3), new ActionRegistry<HorizontalWallRun>(HorizontalWallRun.class, HorizontalWallRun::new, 2), new ActionRegistry<JumpFromBar>(JumpFromBar.class, JumpFromBar::new, 100), new ActionRegistry<QuickTurn>(QuickTurn.class, QuickTurn::new, 0), new ActionRegistry<RideZipline>(RideZipline.class, RideZipline::new, 2), new ActionRegistry<Roll>(Roll.class, Roll::new, 100), new ActionRegistry<SkyDive>(SkyDive.class, SkyDive::new, 0), new ActionRegistry<Slide>(Slide.class, Slide::new, 0), new ActionRegistry<Tap>(Tap.class, Tap::new, 100), new ActionRegistry<Vault>(Vault.class, Vault::new, 50), new ActionRegistry<VerticalWallRun>(VerticalWallRun.class, VerticalWallRun::new, 150), new ActionRegistry<WallJump>(WallJump.class, WallJump::new, 120), new ActionRegistry<WallSlide>(WallSlide.class, WallSlide::new, 8), new ActionRegistry<StartSwimByCrawl>(StartSwimByCrawl.class, StartSwimByCrawl::new, 0));
    private static final HashMap<Class<? extends Action>, Short> INDEX_MAP;
    private static final TreeMap<String, Short> NAME_2_INDEX_MAP;
    public static final List<Class<? extends Action>> LIST;
    public static final List<String> NAMES;

    public static short getIndexOf(Class<? extends Action> action) {
        return INDEX_MAP.getOrDefault(action, (short)-1);
    }

    public static Class<? extends Action> getByIndex(int index) {
        return LIST.get(index);
    }

    @Nullable
    public static Class<? extends Action> getByName(String name) {
        short index = NAME_2_INDEX_MAP.getOrDefault(name, (short)-1);
        if (index == -1) {
            return null;
        }
        return LIST.get(index);
    }

    public static List<Action> constructActionsList() {
        return ACTION_REGISTRIES.stream().map(ActionRegistry::createInstance).collect(Collectors.toList());
    }

    static {
        LIST = ACTION_REGISTRIES.stream().map(ActionRegistry::getClassInstance).collect(Collectors.toList());
        NAMES = LIST.stream().map(Class::getSimpleName).collect(Collectors.toList());
        INDEX_MAP = new HashMap((int)((double)LIST.size() * 1.5));
        NAME_2_INDEX_MAP = new TreeMap();
        for (Class<? extends Action> action : LIST) {
            short index = (short)LIST.indexOf(action);
            INDEX_MAP.put(action, index);
            NAME_2_INDEX_MAP.put(action.getSimpleName(), index);
        }
    }

    public static class ActionRegistry<T extends Action> {
        private final Class<T> classInstance;
        private final Supplier<T> factory;
        private final int defaultStaminaConsumption;

        public ActionRegistry(Class<T> action, Supplier<T> constructor, int defaultStaminaConsumption) {
            this.classInstance = action;
            this.factory = constructor;
            this.defaultStaminaConsumption = defaultStaminaConsumption;
        }

        public Class<T> getClassInstance() {
            return this.classInstance;
        }

        public T createInstance() {
            return (T)((Action)this.factory.get());
        }

        public int getDefaultStaminaConsumption() {
            return this.defaultStaminaConsumption;
        }
    }
}

