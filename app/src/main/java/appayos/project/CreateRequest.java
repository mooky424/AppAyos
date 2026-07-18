package appayos.project;

import android.content.Intent;
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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

public class CreateRequest extends AppCompatActivity {

    //for final proj
    ImageButton createRequest_back; //imagebutton leading back to UserRequestsLists.java

    ImageButton pressToUploadPhoto;//imagebutton leading to SubmitPhoto.java

    EditText requestTitle_input; //where user can input title of request to be created

    EditText requestDescription_input; //where user can input description of request to be created

    Button createRequest_button; //submit button for creating a request
    //end

    private static final int PHOTO_REQUEST_CODE = 1;
    private Realm realm;
    private String selectedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createRequest_back = findViewById(R.id.createRequest_back);
        pressToUploadPhoto = findViewById(R.id.pressToUploadPhoto);
        requestTitle_input = findViewById(R.id.editrequestTitle_input);
        requestDescription_input = findViewById(R.id.requestDescription_input);
        createRequest_button = findViewById(R.id.createRequest_button);
        realm = Realm.getDefaultInstance();

        createRequest_back.setOnClickListener(view -> finish());
        pressToUploadPhoto.setOnClickListener(view -> {
            Intent intent = new Intent(this, SubmitPhoto.class);
            startActivityForResult(intent, PHOTO_REQUEST_CODE);
        });
        createRequest_button.setOnClickListener(view -> saveRequest());
    }

    private void saveRequest() {
        String title = requestTitle_input.getText().toString().trim();
        String description = requestDescription_input.getText().toString().trim();
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and description are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String userUuid = getSharedPreferences("AppAyos", MODE_PRIVATE).getString("user", "");
        if (userUuid.isEmpty()) {
            Toast.makeText(this, "Please sign in again", Toast.LENGTH_SHORT).show();
            return;
        }

        createRequest_button.setEnabled(false);
        realm.executeTransactionAsync(realm -> {
            Request request = realm.createObject(Request.class, UUID.randomUUID().toString());
            request.setUser(userUuid);
            request.setTitle(title);
            request.setDescription(description);
            request.setStatus(Request.STATUSES[0]);
            request.setCreatedAt(new Date());

            if (selectedPhoto != null) {
                Photo photo = realm.createObject(Photo.class, UUID.randomUUID().toString());
                photo.setUploader(userUuid);
                photo.setPath(selectedPhoto);
                photo.setCreatedAt(new Date());
                photo.setType(Photo.TYPES[0]);
                photo.setRequest(request.getUuid());
            }
        }, () -> {
            Toast.makeText(CreateRequest.this, "Request created", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_CODE
                && resultCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN
                && data != null) {
            selectedPhoto = data.getStringExtra("imagePath");
            if (selectedPhoto != null) {
                Picasso.get().load(new File(selectedPhoto)).into(pressToUploadPhoto);
                Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        super.onDestroy();
    }
}
