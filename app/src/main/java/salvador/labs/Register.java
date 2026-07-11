package salvador.labs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.UUID;

import io.realm.Realm;

public class Register extends AppCompatActivity {

    EditText inputUsername;
    EditText inputPassword;
    EditText inputPasswordConfirm;
    ImageView imageIcon;
    Button buttonSave;
    Button buttonCancel;

    Realm realm;
    String selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toast toastBlankName = Toast.makeText(this, "Name must not be blank", Toast.LENGTH_SHORT);
        Toast toastMatchPassword = Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT);
        Toast toastUserExists = Toast.makeText(Register.this, "User already exists", Toast.LENGTH_SHORT);

        inputUsername = findViewById(R.id.username);
        inputPassword = findViewById(R.id.password);
        inputPasswordConfirm = findViewById(R.id.passwordConfirm);
        imageIcon = findViewById(R.id.imageIcon);
        buttonSave = findViewById(R.id.save);
        buttonCancel = findViewById(R.id.cancel);

        realm = Realm.getDefaultInstance();

        imageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, ImageActivity.class);
                startActivityForResult(intent, ImageActivity.RESULT_CODE_IMAGE_TAKEN);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                String passwordConfirm = inputPasswordConfirm.getText().toString();

                if (username.isEmpty()) {
                    toastBlankName.show();
                    return;
                }
                if (!password.equals(passwordConfirm)) {
                    toastMatchPassword.show();
                    return;
                }
                if (realm.where(User.class).equalTo("name",username).findFirst() != null) {
                    toastUserExists.show();
                    return;
                }

                realm.executeTransactionAsync(
                        new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                User user = realm.createObject(User.class, UUID.randomUUID().toString());
                                user.setName(username);
                                user.setPassword(password);
                                user.setImage(selectedImage);
                            }
                        },
                        new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                long total = realm.where(User.class).count();

                                Toast.makeText(
                                        Register.this,
                                        "New User saved. Total: " + total,
                                        Toast.LENGTH_SHORT
                                ).show();

                                realm.close();
                            }
                        });
                finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputUsername.setText("");
                inputPassword.setText("");
                inputPasswordConfirm.setText("");
                selectedImage = null;
                imageIcon.setImageResource(R.mipmap.ic_launcher);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN
                && resultCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN
                && data != null) {
            selectedImage = data.getStringExtra("imagePath");
            loadImage(selectedImage);
        }
    }

    private void loadImage(String image) {
        File imageFile = new File(image);
        if (imageFile.exists()) {
            Picasso.get()
                    .load(imageFile)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageIcon);
        } else {
            imageIcon.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
