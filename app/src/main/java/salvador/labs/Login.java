package salvador.labs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class Login extends AppCompatActivity {

    EditText inputUsername;
    EditText inputPassword;
    CheckBox checkBoxSharedPref;
    Button buttonSignIn;
    Button buttonAdmin;
    Button buttonClear;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toast toastInvalidCreds = Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_SHORT);
        Toast toastNoUser = Toast.makeText(Login.this, "No User Found", Toast.LENGTH_SHORT);
        Toast toastPrefsCleared = Toast.makeText(Login.this, "Preferences Cleared", Toast.LENGTH_SHORT);

        inputUsername = findViewById(R.id.username);
        inputPassword = findViewById(R.id.password);
        checkBoxSharedPref = findViewById(R.id.sharedPref);
        buttonSignIn = findViewById(R.id.signin);
        buttonAdmin = findViewById(R.id.admin);
        buttonClear = findViewById(R.id.clear);

        realm = Realm.getDefaultInstance();
        sharedPrefs = getSharedPreferences("Lab4", MODE_PRIVATE);
        editor = sharedPrefs.edit();

        if (sharedPrefs.getBoolean("rememberMe", false)) {
            String uuid = sharedPrefs.getString("user", "");
            User user = realm.where(User.class).equalTo("uuid", uuid).findFirst();
            inputUsername.setText(user.getName());
            inputPassword.setText(user.getPassword());
            checkBoxSharedPref.setChecked(true);
        }

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                boolean rememberMe = checkBoxSharedPref.isChecked();

                User user = realm.where(User.class).equalTo("name",username).findFirst();

                if (user == null) {
                    toastNoUser.show();
                    return;
                }
                if (!password.equals(user.getPassword())) {
                    toastInvalidCreds.show();
                    return;
                }
                editor.putString("user", user.getUuid());
                editor.putBoolean("rememberMe", rememberMe);
                editor.putString("image", user.getImage());
                editor.apply();

                Intent intent = new Intent(Login.this, Welcome.class);
                startActivity(intent);
            }
        });

        buttonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Admin.class);
                startActivity(intent);
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                sharedPrefs = getSharedPreferences("Lab4", MODE_PRIVATE);
                editor = sharedPrefs.edit();
                editor.clear().apply();
                toastPrefsCleared.show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}