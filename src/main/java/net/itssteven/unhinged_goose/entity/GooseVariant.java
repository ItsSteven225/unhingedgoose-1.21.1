package net.itssteven.unhinged_goose.entity;

import java.util.Arrays;
import java.util.Comparator;

public enum GooseVariant {
    WHITE(0),
    BLACK(1);

    private static final GooseVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(GooseVariant::getId)).toArray(GooseVariant[]::new);
    private final int id;

    GooseVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static GooseVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
