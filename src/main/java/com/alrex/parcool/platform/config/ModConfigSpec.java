package com.alrex.parcool.platform.config;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Minimal stand-in for NeoForge's ModConfigSpec/ModConfigSpec.Builder, covering exactly the
 * builder calls ParCoolConfig.java makes (push/pop/comment/translation/define/defineInRange/
 * defineEnum) so that 741-line file needs no changes beyond its import lines.
 *
 * Persistence: a flat `key=value` properties file under the game's config directory, with
 * `#`-prefixed comment lines written above each entry. Simpler than NeoForge's TOML, but the
 * same nested "push/pop" section names are baked into the dotted key path, so the file still
 * reads as organized sections.
 */
public final class ModConfigSpec {
    private final List<ConfigValue<?>> values;
    private Path filePath;

    private ModConfigSpec(List<ConfigValue<?>> values) {
        this.values = values;
        for (ConfigValue<?> value : values) value.owner = this;
    }

    public void load(Path file) {
        this.filePath = file;
        Properties props = new Properties();
        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file)) {
                props.load(r);
            } catch (IOException e) {
                System.err.println("[ParCool] Failed to load config " + file + ": " + e);
            }
        }
        for (ConfigValue<?> v : values) v.loadFrom(props);
        save(file); // re-write immediately so new/renamed keys and comments show up
    }

    public void save(Path file) {
        this.filePath = file;
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(file)) {
                w.write("# ParCool configuration (Fabric port)\n\n");
                for (ConfigValue<?> v : values) {
                    if (v.comment != null) {
                        for (String line : v.comment.split("\n")) w.write("# " + line + "\n");
                    }
                    w.write(v.path + " = " + v.toStringValue() + "\n\n");
                }
            }
        } catch (IOException e) {
            System.err.println("[ParCool] Failed to save config " + file + ": " + e);
        }
    }

    public static class ConfigValue<T> {
        final String path;
        ModConfigSpec owner;
        String comment;
        String translation;
        T value;
        final T defaultValue;
        private final java.util.function.Function<String, T> parser;
        private final java.util.function.Function<T, String> serializer;

        ConfigValue(String path, T defaultValue,
                    java.util.function.Function<String, T> parser,
                    java.util.function.Function<T, String> serializer) {
            this.path = path;
            this.value = defaultValue;
            this.defaultValue = defaultValue;
            this.parser = parser;
            this.serializer = serializer;
        }

        void loadFrom(Properties props) {
            String raw = props.getProperty(path);
            if (raw != null) {
                try {
                    value = parser.apply(raw.trim());
                } catch (Exception e) {
                    value = defaultValue;
                }
            }
        }

        String toStringValue() { return serializer.apply(value); }

        public T get() { return value; }
        public void set(T v) { this.value = v; }
        public void save() {
            if (owner != null && owner.filePath != null) owner.save(owner.filePath);
        }
    }

    public static final class BooleanValue extends ConfigValue<Boolean> {
        BooleanValue(String path, boolean def) { super(path, def, Boolean::parseBoolean, String::valueOf); }
    }

    public static final class IntValue extends ConfigValue<Integer> {
        IntValue(String path, int def) { super(path, def, Integer::parseInt, String::valueOf); }
    }

    public static final class DoubleValue extends ConfigValue<Double> {
        DoubleValue(String path, double def) { super(path, def, Double::parseDouble, String::valueOf); }
    }

    public static final class EnumValue<E extends Enum<E>> extends ConfigValue<E> {
        EnumValue(String path, E def) {
            super(path, def, s -> Enum.valueOf(def.getDeclaringClass(), s), Enum::name);
        }
    }

    public static final class Builder {
        private final Deque<String> section = new ArrayDeque<>();
        private final List<ConfigValue<?>> values = new ArrayList<>();
        private String pendingComment;
        private String pendingTranslation;

        public Builder push(String name) { section.addLast(name); return this; }
        public Builder pop() { section.removeLast(); return this; }

        public Builder comment(String comment) { pendingComment = comment; return this; }
        public Builder translation(String key) { pendingTranslation = key; return this; }

        private String path(String name) {
            if (section.isEmpty()) return name;
            return String.join(".", section) + "." + name;
        }

        private <T, V extends ConfigValue<T>> V finish(V v) {
            v.comment = pendingComment; pendingComment = null;
            v.translation = pendingTranslation; pendingTranslation = null;
            values.add(v);
            return v;
        }

        public BooleanValue define(String name, boolean def) {
            return finish(new BooleanValue(path(name), def));
        }

        public IntValue defineInRange(String name, int def, int min, int max) {
            return finish(new IntValue(path(name), def));
        }

        public DoubleValue defineInRange(String name, double def, double min, double max) {
            return finish(new DoubleValue(path(name), def));
        }

        public <E extends Enum<E>> EnumValue<E> defineEnum(String name, E def) {
            return finish(new EnumValue<>(path(name), def));
        }

        public ModConfigSpec build() {
            return new ModConfigSpec(values);
        }

        /** Matches NeoForge's {@code Builder.configure(Function<Builder,T>)} -> Pair<T, ModConfigSpec>. */
        public <T> Pair<T, ModConfigSpec> configure(java.util.function.Function<Builder, T> factory) {
            T instance = factory.apply(this);
            return new Pair<>(instance, build());
        }
    }

    /** Tiny stand-in for org.apache.commons.lang3.tuple.Pair, matching getLeft()/getRight(). */
    public static final class Pair<L, R> {
        private final L left;
        private final R right;
        Pair(L left, R right) { this.left = left; this.right = right; }
        public L getLeft() { return left; }
        public R getRight() { return right; }
    }
}
