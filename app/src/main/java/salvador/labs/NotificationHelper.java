package salvador.labs;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class NotificationHelper {

    public static final String CHANNEL_ID = "appayos_notifications";
    private static final String CHANNEL_NAME = "AppAyos Notifications";

    private static final String PREFS = "Lab4";
    private static final String KEY_CURRENT_USER = "user";
    private static final String KEY_ENABLED = "notificationsEnabled";

    private NotificationHelper() {
    }

    // writes async; call from a UI (Looper) thread, OUTSIDE an active Realm transaction
    public static void notifyUser(Context context, String recipientUuid, String message,
                                  String type, String relatedRequestUuid) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(
                new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        writeRow(realm, recipientUuid, message, type, relatedRequestUuid);
                    }
                },
                new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        realm.close();
                        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                        String currentUser = prefs.getString(KEY_CURRENT_USER, "");
                        boolean enabled = prefs.getBoolean(KEY_ENABLED, true);
                        if (enabled && recipientUuid != null && recipientUuid.equals(currentUser)) {
                            postSystemNotification(context, message);
                        }
                    }
                }
        );
    }

    // call from inside an existing transaction (e.g. backend accept/complete)
    public static void createInTransaction(Realm realm, String recipientUuid, String message,
                                           String type, String relatedRequestUuid) {
        writeRow(realm, recipientUuid, message, type, relatedRequestUuid);
    }

    private static void writeRow(Realm realm, String recipientUuid, String message,
                                 String type, String relatedRequestUuid) {
        Notification notification =
                realm.createObject(Notification.class, UUID.randomUUID().toString());
        notification.setRecipient(recipientUuid);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedRequest(relatedRequestUuid);
        notification.setCreatedAt(new Date());
        notification.setRead(false);
    }

    private static void postSystemNotification(Context context, String message) {
        ensureChannel(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("AppAyos")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent openIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (openIntent != null) {
            builder.setContentIntent(PendingIntent.getActivity(
                    context, 0, openIntent, PendingIntent.FLAG_IMMUTABLE));
        }

        int notificationId = (int) (System.currentTimeMillis() & 0x0fffffff);
        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }

    private static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null && manager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Booking request updates");
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void wireBell(AppCompatActivity activity, ImageButton bell) {
        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NotificationsSheet().show(activity.getSupportFragmentManager(), "notifications");
            }
        });
    }

    // returns a Realm the caller must close in onDestroy; badge updates live
    public static Realm observeBadge(Context context, TextView badge) {
        String currentUser = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_CURRENT_USER, "");
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Notification> unread = realm.where(Notification.class)
                .equalTo("recipient", currentUser)
                .equalTo("isRead", false)
                .findAll();
        unread.addChangeListener(new RealmChangeListener<RealmResults<Notification>>() {
            @Override
            public void onChange(RealmResults<Notification> results) {
                renderBadge(badge, results.size());
            }
        });
        renderBadge(badge, unread.size());
        return realm;
    }

    private static void renderBadge(TextView badge, int count) {
        if (count > 0) {
            badge.setText(String.valueOf(count));
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    public static boolean isEnabled(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_ENABLED, true);
    }

    public static void setEnabled(Context context, boolean enabled) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ENABLED, enabled).apply();
    }
}
