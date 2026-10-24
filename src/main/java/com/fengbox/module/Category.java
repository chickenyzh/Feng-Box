package com.fengbox.module;

public enum Category {
    COMBAT("Combat", 0xFFFF5555),
    MOVEMENT("Movement", 0xFF55FF55),
    PLAYER("Player", 0xFF5555FF),
    VISUAL("Visual", 0xFFFFFF55),
    MISC("Misc", 0xFFFF55FF);

    private final String name;
    private final int color;

    Category(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public String getDisplayName() { return com.fengbox.I18n.getCategoryName(name); }
    public int getColor() { return color; }
}
