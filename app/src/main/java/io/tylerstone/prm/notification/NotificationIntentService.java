package io.tylerstone.prm.notification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.tylerstone.prm.MainActivity;
import io.tylerstone.prm.R;
import io.tylerstone.prm.contact.Contact;
import io.tylerstone.prm.support.DateUtils;

/**
 * Created by tyler on 5/7/2017.
 */

public class NotificationIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {

    }

    private void processStartNotification() {

        long today = DateUtils.getToday();
        long tomorrow = DateUtils.getTomorrow();

        // Get all contacts that are due today or past due
        List<Contact> contactsDueToday = Contact.findWithQuery(Contact.class,
                "select * from Contact where reminder_due_date < " + tomorrow);

        if (contactsDueToday.size() > 0) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("PRM")
                    .setAutoCancel(true)
                    .setContentText("You have " + contactsDueToday.size() + " contact reminders today.")
                    .setVibrate(new long[] { 500, 500 })
                    .setLights(Color.BLUE, 1000, 1000)
                    .setSmallIcon(R.drawable.notification_icon);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    NOTIFICATION_ID,
                    new Intent(this, MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

            final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
