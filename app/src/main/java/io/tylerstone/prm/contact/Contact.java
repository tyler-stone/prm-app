package io.tylerstone.prm.contact;

import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tyler on 11/21/2016.
 */

public class Contact extends SugarRecord {
    private int phoneBookId;
    private String name;
    private String occupation;
    private String employer;
    private String email;
    private String phone;

    private Date reminderStartDate;
    private Date reminderDueDate;
    private String reminderFrequency;

    public Contact() {}

    public Contact(String name, String occupation, String employer,
                   String email, String phone) {
        this.name = name;
        this.occupation = occupation;
        this.employer = employer;
        this.email = email;
        this.phone = phone;
    }

    public Contact(String name, String occupation, String employer) {
        this.name = name;
        this.occupation = occupation;
        this.employer = employer;
    }

    public Date applyNextDueDate() {
        if (!reminderFrequency.equals(ReminderFrequency.NEVER) && reminderStartDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            ReminderFrequency reminderFrequencyEnum = ReminderFrequency.getReminderFromText(reminderFrequency);

            switch (reminderFrequencyEnum) {
                case DAILY:
                case WEEKLY:
                case BIWEEKLY:
                    cal.add(Calendar.DAY_OF_YEAR, reminderFrequencyEnum.getIncrement());
                    break;
                case MONTHLY:
                case BIMONTHLY:
                case THREE_MONTHS:
                case SIX_MONTHS:
                    cal.add(Calendar.MONTH, reminderFrequencyEnum.getIncrement());
                    break;
                case YEARLY:
                    cal.add(Calendar.YEAR, reminderFrequencyEnum.getIncrement());
                    break;
                default:
                    break;
            }

            reminderDueDate = cal.getTime();
            return reminderDueDate;
        } else {
            return null;
        }
    }

    public Date snooze() {
        applyNextDueDate();
        this.save();
        return reminderDueDate;
    }

    public void setPhoneBookId(int phoneBookId) { this.phoneBookId = phoneBookId; }

    public int getPhoneBookId() { return phoneBookId; }

    public void setName(String name) { this.name = name; }

    public String getName() {
        return name;
    }

    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getOccupation() {
        return occupation;
    }

    public void setEmployer(String employer) { this.employer = employer; }

    public String getEmployer() {
        return employer;
    }

    public void setEmail(String email) { this.email = email; }

    public String getEmail() {
        return email;
    }

    public void setPhone(String phone) { this.phone = phone; }

    public String getPhone() {
        return phone;
    }

    public void setReminderFrequency(String reminderFrequency) {
        this.reminderFrequency = reminderFrequency;

        if (!reminderFrequency.equals(ReminderFrequency.NEVER.toString())) {
            reminderStartDate = new Date();
            //applyNextDueDate();
            long DAY_IN_MS = 1000 * 60 * 60 * 24;
            reminderDueDate = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
        } else {
            reminderStartDate = null;
            reminderDueDate = null;
        }
    }

    public String getReminderFrequency() { return reminderFrequency; }

    public Date getReminderStartDate() { return reminderStartDate; }

    public Date getReminderDueDate() { return reminderDueDate; }
}
