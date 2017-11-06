package io.tylerstone.prm.contact.interaction;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.util.Date;

import io.tylerstone.prm.contact.Contact;

/**
 * Created by tyler on 3/8/2017.
 */

@Table
public class NoteInteraction extends SugarRecord implements Interaction {
    private String title;
    private Date dateCreated;
    private Contact contact;

    private String note = "";

    public NoteInteraction() {}

    public NoteInteraction(String note, Contact contact) {
        this.dateCreated = new Date();
        this.contact = contact;
        this.note = note;
    }

    public String getTitle() { return title; }

    public String getDetails() {
        return note;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Contact getContact() { return contact; }

    public void setContact(Contact contact) { this.contact = contact; }
}
