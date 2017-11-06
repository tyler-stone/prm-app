package io.tylerstone.prm.contact.interaction;

import java.util.Date;

import io.tylerstone.prm.contact.Contact;

/**
 * Created by tyler on 3/18/2017.
 */

public interface Interaction {
    public String getTitle();
    public String getDetails();
    public Date getDateCreated();
    public Contact getContact();
}
