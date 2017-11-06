package io.tylerstone.prm.contact;

/**
 * Created by tyler on 3/26/2017.
 */

public enum ReminderFrequency {
    NEVER("Never",  0, "never"),
    DAILY("Everyday", 1, "one day"),
    WEEKLY("Every week", 7, "one week"),
    BIWEEKLY("Every other week", 14, "two weeks"),
    MONTHLY("Every month", 1, "one month"),
    BIMONTHLY("Every 2 months", 2, "two months"),
    THREE_MONTHS("Every 3 months", 3, "three months"),
    SIX_MONTHS("Every 6 months", 6, "six months"),
    YEARLY("Every year", 1, "one year");

    private String reminderText;
    private int reminderIncrement;
    private String reminderDurationText;

    ReminderFrequency(String reminderText, int reminderIncrement, String reminderDurationText) {
        this.reminderText = reminderText;
        this.reminderIncrement = reminderIncrement;
        this.reminderDurationText = reminderDurationText;
    }

    public static ReminderFrequency getReminderFromText(String text) {
        switch (text) {
            case "Never":
                return NEVER;
            case "Everyday":
                return DAILY;
            case "Every week":
                return WEEKLY;
            case "Every other week":
                return BIWEEKLY;
            case "Every month":
                return MONTHLY;
            case "Every 2 months":
                return BIMONTHLY;
            case "Every 3 months":
                return THREE_MONTHS;
            case "Every 6 months":
                return SIX_MONTHS;
            case "Every year":
                return YEARLY;
            default:
                return NEVER;
        }
    }

    public int getIncrement() { return reminderIncrement; }
    public String getReminderDurationText() { return reminderDurationText; }

    public String toString() {
        return reminderText;
    }
}
