package io.tylerstone.prm.contact.interaction;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import io.tylerstone.prm.R;

/**
 * Created by tyler on 3/8/2017.
 */

public class InteractionListAdapter extends ArrayAdapter {
    private final Activity activity;
    private final List<Interaction> list;


    public InteractionListAdapter(Activity activity, List<Interaction> list) {
        super(activity, 0, list);
        this.activity = activity;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder view;

        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listitem_interaction, null);

            view = new ViewHolder();
            view.typeImage = (ImageView) rowView.findViewById(R.id.interaction_type_image);
            view.title = (TextView) rowView.findViewById(R.id.interaction_title);
            view.date = (TextView) rowView.findViewById(R.id.interaction_date);
            view.details = (TextView) rowView.findViewById(R.id.interaction_details);
            view.tagNoteDetails = (LinearLayout) rowView.findViewById(R.id.interaction_tag_note_details);
            view.tags = (TextView) rowView.findViewById(R.id.interaction_tags);
            view.notes = (TextView) rowView.findViewById(R.id.interaction_notes);
            view.interactionActions = (RelativeLayout) rowView.findViewById(R.id.interaction_actions);

            rowView.setTag(view);
        } else {
            view = (ViewHolder) rowView.getTag();
        }

        Interaction item = (Interaction) list.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");

        view.title.setText(item.getTitle());
        view.date.setText(dateFormat.format(item.getDateCreated()));
        view.details.setText(item.getDetails());

        if (item instanceof NoteInteraction) {
            view.interactionActions.setVisibility(View.GONE);
            view.tagNoteDetails.setVisibility(View.GONE);
        }

        if (item instanceof PhoneInteraction) {
            view.title.setText("Phone Call");
            view.typeImage.setImageResource(R.drawable.phone);
        } else if (item instanceof TextMessageInteraction) {
            view.title.setText("Text Conversation");
            view.typeImage.setImageResource(R.drawable.conversation);
        } else if (item instanceof NoteInteraction) {
            view.title.setText("Note");
            view.typeImage.setImageResource(R.drawable.note);
        } else {
            view.typeImage.setImageResource(R.drawable.warning);
        }

        return rowView;
    }

    protected static class ViewHolder {
        protected ImageView typeImage;
        protected TextView title;
        protected TextView date;
        protected TextView details;
        protected LinearLayout tagNoteDetails;
        protected RelativeLayout interactionActions;
        protected TextView tags;
        protected TextView notes;
    }
}
