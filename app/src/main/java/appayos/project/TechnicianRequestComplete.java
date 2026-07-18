package appayos.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class TechnicianRequestComplete extends AppCompatActivity {

    //for final proj

    ImageButton completeRequest_back;

    TextView completeRequest_prefilledRequestname; //textview where specific request name should be displayed

    TextView completeRequest_prefilledRequestDescription; //textview where specific request description should be displayed

    ImageButton pressToUploadphoto2; //leads to SubmitPhoto.java

    EditText completeRequest_statusDescription;

    Button submitPhoto_submit2; //submit button for Request to be "completed"
    //end

    private static final int PHOTO_REQUEST_CODE = 1;
    private Realm realm;
    private String requestUuid;
    private String technicianUuid;
    private String selectedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_technician_request_complete);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        completeRequest_back = findViewById(R.id.completeRequest_back);
        completeRequest_prefilledRequestname = findViewById(R.id.completeRequest_prefilledRequestname);
        completeRequest_prefilledRequestDescription =
                findViewById(R.id.completeRequest_prefilledRequestDescription);
        pressToUploadphoto2 = findViewById(R.id.pressToUploadphoto2);
        completeRequest_statusDescription = findViewById(R.id.completeRequest_statusDescription);
        submitPhoto_submit2 = findViewById(R.id.submitPhoto_submit2);
        realm = Realm.getDefaultInstance();
        requestUuid = getIntent().getStringExtra("REQUEST_UUID");
        technicianUuid = getSharedPreferences("AppAyos", MODE_PRIVATE)
                .getString("user", "");

        Request request = realm.where(Request.class)
                .equalTo("uuid", requestUuid)
                .equalTo("status", Request.STATUSES[1])
                .equalTo("acceptedBy", technicianUuid)
                .findFirst();
        if (request == null) {
            Toast.makeText(this, "Request not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        completeRequest_prefilledRequestname.setText(request.getTitle());
        completeRequest_prefilledRequestDescription.setText(request.getDescription());

        completeRequest_back.setOnClickListener(view -> finish());
        pressToUploadphoto2.setOnClickListener(view -> {
            Intent intent = new Intent(this, SubmitPhoto.class);
            startActivityForResult(intent, PHOTO_REQUEST_CODE);
        });
        submitPhoto_submit2.setOnClickListener(view -> completeRequest());
    }

    private void completeRequest() {
        String statusDescription = completeRequest_statusDescription.getText().toString().trim();
        if (selectedPhoto == null || statusDescription.isEmpty()) {
            Toast.makeText(this, "Completion photo and description are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        submitPhoto_submit2.setEnabled(false);
        realm.executeTransactionAsync(realm -> {
            Request request = realm.where(Request.class)
                    .equalTo("uuid", requestUuid)
                    .equalTo("status", Request.STATUSES[1])
                    .equalTo("acceptedBy", technicianUuid)
                    .findFirst();
            if (request == null) {
                return;
            }
            request.setStatus(Request.STATUSES[2]);
            request.setStatusDescription(statusDescription);

            Photo photo = realm.createObject(Photo.class, UUID.randomUUID().toString());
            photo.setUploader(technicianUuid);
            photo.setPath(selectedPhoto);
            photo.setCreatedAt(new Date());
            photo.setType(Photo.TYPES[1]);
            photo.setRequest(requestUuid);

            NotificationHelper.createInTransaction(
                    realm,
                    request.getUser(),
                    "Your request \"" + request.getTitle() + "\" was completed",
                    Notification.TYPE_COMPLETED,
                    requestUuid);
        }, () -> {
            Toast.makeText(this, "Request completed", Toast.LENGTH_SHORT).show();
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
                Picasso.get().load(new File(selectedPhoto)).into(pressToUploadphoto2);
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
