package appayos.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class NotificationsSheet extends BottomSheetDialogFragment {

    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_notifications, container, false);

        realm = Realm.getDefaultInstance();
        String currentUser = requireContext()
                .getSharedPreferences("AppAyos", 0)
                .getString("user", "");

        RecyclerView recycler = view.findViewById(R.id.notifications_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        RealmResults<Notification> notifications = realm.where(Notification.class)
                .equalTo("recipient", currentUser)
                .sort("createdAt", Sort.DESCENDING)
                .findAll();
        recycler.setAdapter(new NotificationAdapter(getContext(), notifications));

        View empty = view.findViewById(R.id.notifications_empty);
        updateEmpty(empty, recycler, notifications.size());
        notifications.addChangeListener(new io.realm.RealmChangeListener<RealmResults<Notification>>() {
            @Override
            public void onChange(RealmResults<Notification> results) {
                updateEmpty(empty, recycler, results.size());
            }
        });

        markAllRead(currentUser);

        // TEMP: remove once backend calls NotificationHelper.notifyUser
        Button testButton = view.findViewById(R.id.notifications_test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationHelper.notifyUser(
                        requireContext(),
                        currentUser,
                        "Your request was accepted by a technician",
                        Notification.TYPE_ACCEPTED,
                        UUID.randomUUID().toString());
            }
        });

        return view;
    }

    private void updateEmpty(View empty, RecyclerView recycler, int count) {
        empty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        recycler.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
    }

    private void markAllRead(String currentUser) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Notification> unread = realm.where(Notification.class)
                        .equalTo("recipient", currentUser)
                        .equalTo("isRead", false)
                        .findAll();
                for (Notification notification : unread) {
                    notification.setRead(true);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null) {
            realm.close();
        }
    }
}
