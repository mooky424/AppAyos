package salvador.labs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateRequest extends AppCompatActivity {

    //for final proj
    ImageButton createRequest_back; //imagebutton leading back to UserRequestsLists.java

    ImageButton pressToUploadPhoto;//imagebutton leading to SubmitPhoto.java

    EditText requestTitle_input; //where user can input title of request to be created

    EditText requestDescription_input; //where user can input description of request to be created

    Button createRequest_button; //submit button for creating a request
    //end

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
    }
}