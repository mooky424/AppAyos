package appayos.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class UsersList extends AppCompatActivity {
    public static final String EXTRA_ADMIN_BYPASS = "admin_bypass";

    private ImageButton backButton;
    private RecyclerView usersRecycler;
    private ImageButton addUserButton;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.userslist_back);
        usersRecycler = findViewById(R.id.users);
        addUserButton = findViewById(R.id.addUser);
        realm = Realm.getDefaultInstance();

        String currentUserUuid = getSharedPreferences("AppAyos", MODE_PRIVATE)
                .getString("user", "");
        User currentUser = realm.where(User.class)
                .equalTo("uuid", currentUserUuid)
                .findFirst();
        boolean adminBypass = getIntent().getBooleanExtra(EXTRA_ADMIN_BYPASS, false);
        if (!adminBypass && (currentUser == null || !currentUser.isAdmin())) {
            finish();
            return;
        }

        RealmResults<User> userResults = realm.where(User.class).findAll();
        usersRecycler.setLayoutManager(new LinearLayoutManager(this));
        usersRecycler.setAdapter(new UserAdapter(this, userResults, userUuid -> {
            Intent intent = new Intent(UsersList.this, AdminEditUser.class);
            intent.putExtra("UUID", userUuid);
            startActivity(intent);
        }));

        backButton.setOnClickListener(view -> finish());
        addUserButton.setOnClickListener(view ->
                startActivity(new Intent(this, Register.class)));
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
