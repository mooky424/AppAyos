package appayos.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class Tracker extends AppCompatActivity {

    //for final proj
    RecyclerView recyclerViewrequests; //shows all existing Requests

    Button SeeAcceptedRequests; //button leading to PendingRequests.java

    ImageButton home_bottomnav; //bottom navigation button leading to UserRequestsList.java

    ImageButton tracker_bottomnav; //bottom navigation button leading to Tracker.java

    ImageButton settings_bottomnav; //bottom navigation button leading to Settings.java
    //end

    ImageButton notificationBell;
    TextView notificationBadge;
    Realm badgeRealm;
    Realm realm;
    String technicianUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tracker_requests_made);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        notificationBell = findViewById(R.id.notificationBell);
        notificationBadge = findViewById(R.id.notificationBadge);
        NotificationHelper.wireBell(this, notificationBell);
        badgeRealm = NotificationHelper.observeBadge(this, notificationBadge);

        recyclerViewrequests = findViewById(R.id.recyclerViewrequests);
        SeeAcceptedRequests = findViewById(R.id.SeeAcceptedRequests);
        home_bottomnav = findViewById(R.id.home_bottomnav);
        tracker_bottomnav = findViewById(R.id.tracker_bottomnav);
        settings_bottomnav = findViewById(R.id.settings_bottomnav);
        realm = Realm.getDefaultInstance();
        technicianUuid = getSharedPreferences("AppAyos", MODE_PRIVATE)
                .getString("user", "");
        User currentUser = realm.where(User.class)
                .equalTo("uuid", technicianUuid)
                .findFirst();
        if (currentUser == null || !currentUser.isTechnician()) {
            finish();
            return;
        }

        RealmResults<Request> requests = realm.where(Request.class)
                .equalTo("status", Request.STATUSES[0])
                .sort("createdAt", Sort.DESCENDING)
                .findAll();
        recyclerViewrequests.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewrequests.setAdapter(new TrackerRequestAdapter(this, realm, requests));

        SeeAcceptedRequests.setOnClickListener(view ->
                startActivity(new Intent(this, PendingRequests.class)));
        home_bottomnav.setOnClickListener(view ->
                startActivity(new Intent(this, UserRequestsLists.class)));
        tracker_bottomnav.setOnClickListener(view -> recreate());
        settings_bottomnav.setOnClickListener(view ->
                startActivity(new Intent(this, Settings.class)));
    }

    public void acceptRequest(String requestUuid) {
        realm.executeTransactionAsync(realm -> {
            Request request = realm.where(Request.class)
                    .equalTo("uuid", requestUuid)
                    .equalTo("status", Request.STATUSES[0])
                    .findFirst();
            if (request == null) {
                return;
            }
            request.setStatus(Request.STATUSES[1]);
            request.setAcceptedBy(technicianUuid);
            NotificationHelper.createInTransaction(
                    realm,
                    request.getUser(),
                    "Your request \"" + request.getTitle() + "\" was accepted",
                    Notification.TYPE_ACCEPTED,
                    request.getUuid());
        }, () -> Toast.makeText(this, "Request accepted", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        badgeRealm.close();
        realm.close();
        super.onDestroy();
    }
}
