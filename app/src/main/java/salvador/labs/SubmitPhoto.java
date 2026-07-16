package salvador.labs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.theartofdev.edmodo.cropper.CropImageView;

public class SubmitPhoto extends AppCompatActivity {

    //for final proj
    ImageButton submitPhoto_back; //goes back to which page they came from (CreateRequest.java,EditRequest.java, or CompleteRequest.java)

    FrameLayout submitPhoto_FrameLayout; //frame layout for submitPhoto_cropImageView

    CropImageView submitPhoto_cropImageView; //CropImageView for photo

    Button take; //take photo

    Button retake; //retake photo

    Button submitPhoto_browse;//browse files

    Button submitPhoto_submit; //submit photo
    //end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_photo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}