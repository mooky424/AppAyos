package salvador.labs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.UUID;

import io.realm.Realm;

public class Register extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputPasswordConfirm;
    private CheckBox technicianCheckbox;
    private Button buttonSave;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputUsername = findViewById(R.id.username);
        inputPassword = findViewById(R.id.password);
        inputPasswordConfirm = findViewById(R.id.passwordConfirm);
        technicianCheckbox = findViewById(R.id.checkBox);
        buttonSave = findViewById(R.id.save);
        realm = Realm.getDefaultInstance();

        buttonSave.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString();
        String passwordConfirm = inputPasswordConfirm.getText().toString();
        boolean isTechnician = technicianCheckbox.isChecked();

        if (username.isEmpty()) {
            Toast.makeText(this, "Name must not be blank", Toast.LENGTH_SHORT).show();
            inputUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Password must not be blank", Toast.LENGTH_SHORT).show();
            inputPassword.requestFocus();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT).show();
            inputPasswordConfirm.requestFocus();
            return;
        }
        if (realm.where(User.class).equalTo("username", username).findFirst() != null) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            inputUsername.requestFocus();
            return;
        }

        buttonSave.setEnabled(false);
        realm.executeTransactionAsync(
                new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm) {
                        User user = realm.createObject(User.class, UUID.randomUUID().toString());
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setTechnician(isTechnician);
                        user.setAdmin(false);
                        user.setImage(null);
                        }
                },
                new Realm.Transaction.OnSuccess(){
                    @Override
                    public void onSuccess() {
                        Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();
                        realm.close();
                        finish();
                    }
                }
        );
    }
}
