package appayos.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class UserEditDetail extends AppCompatActivity {
    private ImageButton backButton;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText passwordConfirmInput;
    private Button saveButton;
    private Realm realm;
    private String userUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_edit_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.editUser_back);
        usernameInput = findViewById(R.id.changeUsername_input);
        passwordInput = findViewById(R.id.changePassword_input);
        passwordConfirmInput = findViewById(R.id.changePasswordConfirm_input);
        saveButton = findViewById(R.id.saveEditedUser);
        realm = Realm.getDefaultInstance();
        userUuid = getIntent().getStringExtra("UUID");
        if (userUuid == null || userUuid.isEmpty()) {
            userUuid = getSharedPreferences("AppAyos", MODE_PRIVATE)
                    .getString("user", "");
        }

        User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();
        if (user == null) {
            finish();
            return;
        }
        usernameInput.setText(user.getUsername());
        passwordInput.setText(user.getPassword());
        passwordConfirmInput.setText(user.getPassword());

        backButton.setOnClickListener(view -> finish());
        saveButton.setOnClickListener(view -> saveUser());
    }

    private void saveUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String passwordConfirm = passwordConfirmInput.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Password confirmation does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        User existingUser = realm.where(User.class).equalTo("username", username).findFirst();
        if (existingUser != null && !existingUser.getUuid().equals(userUuid)) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        saveButton.setEnabled(false);
        realm.executeTransactionAsync(realm -> {
            User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();
            if (user != null) {
                user.setUsername(username);
                user.setPassword(password);
            }
        }, () -> {
            Toast.makeText(this, "User details updated", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        super.onDestroy();
    }
}
