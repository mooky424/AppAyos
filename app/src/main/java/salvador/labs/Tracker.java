package salvador.labs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class Tracker extends AppCompatActivity {

    //for final proj
    RecyclerView recyclerViewrequests; //shows all existing Requests

    Button SeeAcceptedRequests; //button leading to PendingRequests.java

    ImageButton home_bottomnav; //bottom navigation button leading to UserRequestsList.java

    ImageButton tracker_bottomnav; //bottom navigation button leading to Tracker.java

    ImageButton settings_bottomnav; //bottom navigation button leading to Settings.java
    //end

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_requests_made);
    }
}