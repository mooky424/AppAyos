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

public class UserRequestsLists extends AppCompatActivity {

    //for final proj
    RecyclerView recyclerViewrequests; //shows all existing requests

    ImageButton createRequest; //button leading to CreateRequest.java

    ImageButton home_bottomnav; //bottom navigation button leading to UserRequestsList.java

    ImageButton tracker_bottomnav; //bottom navigation button leading to Tracker.java

    ImageButton settings_bottomnav; //bottom navigation button leading to Settings.java
    //end

    ImageButton notificationBell;
    TextView notificationBadge;
    Realm badgeRealm;
    Realm realm;
    RequestAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_requests_lists);
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
        createRequest = findViewById(R.id.createRequest);
        home_bottomnav = findViewById(R.id.home_bottomnav);
        tracker_bottomnav = findViewById(R.id.tracker_bottomnav);
        settings_bottomnav = findViewById(R.id.settings_bottomnav);

        realm = Realm.getDefaultInstance();
        String userUuid = getSharedPreferences("AppAyos", MODE_PRIVATE)
                .getString("user", "");
        RealmResults<Request> requests = realm.where(Request.class)
                .equalTo("user", userUuid)
                .sort("createdAt", Sort.DESCENDING)
                .findAll();
        recyclerViewrequests.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new RequestAdapter(this, realm, requests);
        recyclerViewrequests.setAdapter(requestAdapter);

        createRequest.setOnClickListener(view ->
                startActivity(new Intent(this, CreateRequest.class)));
        home_bottomnav.setOnClickListener(view -> recreate());
        tracker_bottomnav.setOnClickListener(view ->
                startActivity(new Intent(this, Tracker.class)));
        settings_bottomnav.setOnClickListener(view ->
                startActivity(new Intent(this, Settings.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestAdapter != null) {
            requestAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        badgeRealm.close();
        super.onDestroy();
    }
}
