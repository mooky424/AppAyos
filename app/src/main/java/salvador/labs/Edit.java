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

import io.realm.Realm;

public class Edit extends AppCompatActivity {

    EditText inputUsername;
    EditText inputPassword;
    EditText inputPasswordConfirm;
    ImageView imagePreview;
    Button buttonSave;
    Button buttonCancel;

    Realm realm;
    String selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toast toastBlankName = Toast.makeText(this, "Name must not be blank", Toast.LENGTH_SHORT);
        Toast toastMatchPassword = Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT);
        Toast toastUserExists = Toast.makeText(Edit.this, "User already exists", Toast.LENGTH_SHORT);

        inputUsername = findViewById(R.id.username);
        inputPassword = findViewById(R.id.password);
        inputPasswordConfirm = findViewById(R.id.passwordConfirm);
        imagePreview = findViewById(R.id.imageIcon);
        buttonSave = findViewById(R.id.save);
        buttonCancel = findViewById(R.id.cancel);

        realm = Realm.getDefaultInstance();

        String userUuid = getIntent().getStringExtra("UUID");
        User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();

        inputUsername.setText(user.getUsername());
        inputPassword.setText(user.getPassword());
        inputPasswordConfirm.setText(user.getPassword());
        selectedImage = user.getImage();
        loadImage(selectedImage);

        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Edit.this, ImageActivity.class);
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

                User existingUser = realm.where(User.class).equalTo("username", username).findFirst();
                if (existingUser != null && !existingUser.getUuid().equals(userUuid)) {
                    toastUserExists.show();
                    return;
                }

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setImage(selectedImage);
                    }
                });

                finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    .into(imagePreview);
        } else {
            imagePreview.setImageResource(R.mipmap.ic_launcher);
        }
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
