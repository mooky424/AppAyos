package salvador.labs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;

public class Settings extends AppCompatActivity {

    //for final proj
    Button SeeAllUsers; //leads to UsersList.java

    RecyclerView RecyclerViewOFUsernames; //RecyclerView of just usernames (layout is usernames_rows.xml)

    EditText changeUsername_input; //input box where user can change their username

    EditText changePassword_input; //input box where user can change their password

    ImageButton home_bottomnav; //bottom navigation button leading to UserRequestsList.java

    ImageButton tracker_bottomnav; //bottom navigation button leading to Tracker.java

    ImageButton settings_bottomnav; //bottom navigation button leading to Settings.java
    //end

    ImageButton notificationBell;
    TextView notificationBadge;
    SwitchCompat notificationToggle;
    Realm badgeRealm;

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

        notificationToggle = findViewById(R.id.notificationToggle);
        notificationToggle.setChecked(NotificationHelper.isEnabled(this));
        notificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationHelper.setEnabled(Settings.this, isChecked);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (badgeRealm != null) {
            badgeRealm.close();
        }
        super.onDestroy();
    }
}