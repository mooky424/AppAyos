package salvador.labs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class Login extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;
    private Button buttonSignIn;
    private Button buttonRegister;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputUsername = findViewById(R.id.username);
        inputPassword = findViewById(R.id.password);
        buttonSignIn = findViewById(R.id.signin);
        buttonRegister = findViewById(R.id.register);
        realm = Realm.getDefaultInstance();

        buttonSignIn.setOnClickListener(view -> signIn());
        buttonRegister.setOnClickListener(view ->
                startActivity(new Intent(Login.this, Register.class)));
    }

    private void signIn() {
        String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            inputUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            inputPassword.requestFocus();
            return;
        }

        User user = realm.where(User.class).equalTo("username", username).findFirst();
        if (user == null || !password.equals(user.getPassword())) {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences("Lab4", MODE_PRIVATE).edit();
        editor.putString("user", user.getUuid());
        editor.putString("image", user.getImage());
        editor.apply();

        Class<?> destination;
        if (user.isAdmin()) {
            destination = Admin.class;
        } else if (user.isTechnician()) {
            destination = Tracker.class;
        } else {
            destination = UserRequestsLists.class;
        }

        Intent intent = new Intent(this, destination);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
