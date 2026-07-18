package appayos.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class UserPendingRequestList extends AppCompatActivity {

    //for final proj
    RecyclerView recyclerViewpendingRequests; //recyclerview for all Requests not completed (the layout is pending_user_requests_rows.xml)
    //end

    Realm realm;
    ImageButton home_bottomnav;
    ImageButton tracker_bottomnav;
    ImageButton settings_bottomnav;
    ImageButton notificationBell;
    TextView notificationBadge;
    Realm badgeRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_pending_request_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        notificationBell = findViewById(R.id.notificationBell);
        notificationBadge = findViewById(R.id.notificationBadge);
        NotificationHelper.wireBell(this, notificationBell);
        badgeRealm = NotificationHelper.observeBadge(this, notificationBadge);

        recyclerViewpendingRequests = findViewById(R.id.recyclerViewpendingRequests);
        home_bottomnav = findViewById(R.id.home_bottomnav);
        tracker_bottomnav = findViewById(R.id.tracker_bottomnav);
        settings_bottomnav = findViewById(R.id.settings_bottomnav);
        realm = Realm.getDefaultInstance();
        String technicianUuid = getSharedPreferences("AppAyos", MODE_PRIVATE)
                .getString("user", "");
        RealmResults<Request> requests = realm.where(Request.class)
                .equalTo("status", Request.STATUSES[1])
                .equalTo("acceptedBy", technicianUuid)
                .sort("createdAt", Sort.DESCENDING)
                .findAll();
        recyclerViewpendingRequests.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewpendingRequests.setAdapter(
                new UserPendingRequestAdapter(this, realm, requests));
        home_bottomnav.setOnClickListener(view ->
                startActivity(new Intent(this, TechnicianRequestList.class)));
        tracker_bottomnav.setOnClickListener(view -> recreate());
        settings_bottomnav.setOnClickListener(view ->
                startActivity(new Intent(this, Settings.class)));
    }

    @Override
    protected void onDestroy() {
        if (badgeRealm != null && !badgeRealm.isClosed()) {
            badgeRealm.close();
        }
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        super.onDestroy();
    }
}
