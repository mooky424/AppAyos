package appayos.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import io.realm.Sort;

public class UserRequestEdit extends AppCompatActivity {

    //for final proj
    ImageButton editRequest_back; //imagebutton leads back to UserRequestList.java

    ImageButton pressToUploadPhoto3;//user submits different image here

    EditText editrequestTitle_input; //user edits title here

    EditText editrequestDescription_input3; //user edits description here

    ImageView editRequest_completionPhoto;
    TextView editRequest_statusDescriptionLabel;
    TextView editRequest_statusDescription;

    Button editRequest_button3; //submit edited request
    //end

    private static final int PHOTO_REQUEST_CODE = 1;
    private Realm realm;
    private String requestUuid;
    private String userUuid;
    private String selectedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_request_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editRequest_back = findViewById(R.id.editRequest_back);
        pressToUploadPhoto3 = findViewById(R.id.pressToUploadphoto3);
        editrequestTitle_input = findViewById(R.id.editrequestTitle_input);
        editrequestDescription_input3 = findViewById(R.id.editrequestDescription_input3);
        editRequest_completionPhoto = findViewById(R.id.editRequest_completionPhoto);
        editRequest_statusDescriptionLabel = findViewById(R.id.editRequest_statusDescriptionLabel);
        editRequest_statusDescription = findViewById(R.id.editRequest_statusDescription);
        editRequest_button3 = findViewById(R.id.editRequest_button3);
        realm = Realm.getDefaultInstance();
        requestUuid = getIntent().getStringExtra("REQUEST_UUID");
        userUuid = getSharedPreferences("AppAyos", MODE_PRIVATE).getString("user", "");

        Request request = realm.where(Request.class)
                .equalTo("uuid", requestUuid)
                .equalTo("user", userUuid)
                .findFirst();
        if (request == null) {
            Toast.makeText(this, "Request not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        editrequestTitle_input.setText(request.getTitle());
        editrequestDescription_input3.setText(request.getDescription());

        Photo requestPhoto = realm.where(Photo.class)
                .equalTo("request", requestUuid)
                .equalTo("type", Photo.TYPES[0])
                .sort("createdAt", Sort.DESCENDING)
                .findFirst();
        if (requestPhoto != null) {
            showPhoto(requestPhoto.getPath());
        }

        if (Request.STATUSES[2].equals(request.getStatus())) {
            editrequestTitle_input.setEnabled(false);
            editrequestDescription_input3.setEnabled(false);
            pressToUploadPhoto3.setEnabled(false);
            editRequest_button3.setEnabled(false);

            Photo completionPhoto = realm.where(Photo.class)
                    .equalTo("request", requestUuid)
                    .equalTo("type", Photo.TYPES[1])
                    .sort("createdAt", Sort.DESCENDING)
                    .findFirst();
            if (completionPhoto != null && completionPhoto.getPath() != null) {
                showCompletionPhoto(completionPhoto.getPath());
            }
            if (request.getStatusDescription() != null
                    && !request.getStatusDescription().trim().isEmpty()) {
                editRequest_statusDescriptionLabel.setVisibility(TextView.VISIBLE);
                editRequest_statusDescription.setText(request.getStatusDescription());
                editRequest_statusDescription.setVisibility(TextView.VISIBLE);
            }
        }

        editRequest_back.setOnClickListener(view -> finish());
        pressToUploadPhoto3.setOnClickListener(view -> {
            Intent intent = new Intent(this, SubmitPhoto.class);
            startActivityForResult(intent, PHOTO_REQUEST_CODE);
        });
        editRequest_button3.setOnClickListener(view -> saveRequest());
    }

    private void saveRequest() {
        String title = editrequestTitle_input.getText().toString().trim();
        String description = editrequestDescription_input3.getText().toString().trim();
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and description are required", Toast.LENGTH_SHORT).show();
            return;
        }

        editRequest_button3.setEnabled(false);
        realm.executeTransactionAsync(realm -> {
            Request request = realm.where(Request.class)
                    .equalTo("uuid", requestUuid)
                    .equalTo("user", userUuid)
                    .findFirst();
            if (request == null) {
                return;
            }
            request.setTitle(title);
            request.setDescription(description);

            if (selectedPhoto != null) {
                Photo photo = realm.createObject(Photo.class, UUID.randomUUID().toString());
                photo.setUploader(userUuid);
                photo.setPath(selectedPhoto);
                photo.setCreatedAt(new Date());
                photo.setType(Photo.TYPES[0]);
                photo.setRequest(requestUuid);
            }
        }, () -> {
            Toast.makeText(UserRequestEdit.this, "Request updated", Toast.LENGTH_SHORT).show();
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
                showPhoto(selectedPhoto);
                Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPhoto(String imagePath) {
        Picasso.get().load(new File(imagePath)).into(pressToUploadPhoto3);
    }

    private void showCompletionPhoto(String imagePath) {
        editRequest_completionPhoto.setVisibility(ImageView.VISIBLE);
        Picasso.get().load(new File(imagePath)).into(editRequest_completionPhoto);
    }

    @Override
    protected void onDestroy() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        super.onDestroy();
    }
}
