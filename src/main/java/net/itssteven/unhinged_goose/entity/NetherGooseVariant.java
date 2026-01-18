package net.itssteven.unhinged_goose.entity;

import java.util.Arrays;
import java.util.Comparator;

public enum NetherGooseVariant {
    WHITE(0),
    BLACK(1);

    private static final NetherGooseVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(NetherGooseVariant::getId)).toArray(NetherGooseVariant[]::new);
    private final int id;

    NetherGooseVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static NetherGooseVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
