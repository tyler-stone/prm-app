package io.tylerstone.prm.contact.list;

import io.tylerstone.prm.contact.Contact;

/**
 * Created by tyler on 5/14/2017.
 */

public abstract class ContactListItemActionListener {
    public abstract void requestUpdate();
    public abstract void contactNoteAction(Contact contact);
    public abstract void contactFlagAction(Contact contact);
}
