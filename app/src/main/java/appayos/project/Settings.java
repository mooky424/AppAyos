package appayos.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class Settings extends AppCompatActivity {
    private Button seeAllUsers;
    private ImageButton homeBottomNav;
    private ImageButton trackerBottomNav;
    private ImageButton settingsBottomNav;
    private ImageButton notificationBell;
    private TextView notificationBadge;
    private SwitchCompat notificationToggle;
    private Realm badgeRealm;
    private Realm realm;
    private String userUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        notificationBell = findViewById(R.id.notificationBell);
        notificationBadge = findViewById(R.id.notificationBadge);
        NotificationHelper.wireBell(this, notificationBell);
        badgeRealm = NotificationHelper.observeBadge(this, notificationBadge);

        seeAllUsers = findViewById(R.id.SeeAllUsers);
        Button changeUserDetailsButton = findViewById(R.id.changeUserDetails);
        Button logoutButton = findViewById(R.id.logout);
        homeBottomNav = findViewById(R.id.home_bottomnav);
        trackerBottomNav = findViewById(R.id.tracker_bottomnav);
        settingsBottomNav = findViewById(R.id.settings_bottomnav);
        notificationToggle = findViewById(R.id.notificationToggle);

        realm = Realm.getDefaultInstance();
        userUuid = getSharedPreferences("AppAyos", MODE_PRIVATE)
                .getString("user", "");
        User currentUser = realm.where(User.class)
                .equalTo("uuid", userUuid)
                .findFirst();
        if (currentUser == null) {
            finish();
            return;
        }

        if (!currentUser.isAdmin()) {
            findViewById(R.id.AdminOnly).setVisibility(TextView.GONE);
            seeAllUsers.setVisibility(Button.GONE);
        } else {
            seeAllUsers.setOnClickListener(view ->
                    startActivity(new Intent(this, AdminUserList.class)));
        }

        changeUserDetailsButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, UserEditDetail.class);
            intent.putExtra("UUID", userUuid);
            startActivity(intent);
        });
        logoutButton.setOnClickListener(view -> logout());
        Class<?> dashboard = currentUser.isTechnician()
                ? TechnicianRequestList.class
                : UserRequestList.class;
        homeBottomNav.setOnClickListener(view ->
                startActivity(new Intent(this, dashboard)));
        trackerBottomNav.setOnClickListener(view ->
                startActivity(new Intent(this,
                        currentUser.isTechnician() ? UserPendingRequestList.class : TechnicianRequestList.class)));
        settingsBottomNav.setOnClickListener(view -> recreate());

        notificationToggle.setChecked(NotificationHelper.isEnabled(this));
        notificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationHelper.setEnabled(Settings.this, isChecked);
            }
        });
    }

    private void logout() {
        getSharedPreferences("AppAyos", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
