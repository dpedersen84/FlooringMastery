package com.dp.flooringmastery.ui;

public enum MainMenu {
    DISPLAY_ORDERS(1, "Display Orders"),
    ADD_ORDER(2, "Add an Order"),
    EDIT_ORDER(3, "Edit an Order"),
    REMOVE_ORDER(4, "Remove an Order"),
    QUIT(5, "Quit");

    private final int value;
    private final String option;

    private MainMenu(int value, String option) {
        this.value = value;
        this.option = option;
    }

    public int getValue() {
        return value;
    }

    public String getOption() {
        return option;
    }

    public static MainMenu fromValue(int value) {
        for (MainMenu mmo : MainMenu.values()) {
            if (mmo.getValue() == value) {
                return mmo;
            }
        }
        return QUIT;
    }
}
