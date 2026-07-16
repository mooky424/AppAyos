package salvador.labs;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class UserRequestsLists extends AppCompatActivity {

    //for final proj
    RecyclerView recyclerViewrequests; //shows all existing requests

    ImageButton createRequest; //button leading to CreateRequest.java

    ImageButton home_bottomnav; //bottom navigation button leading to UserRequestsList.java

    ImageButton tracker_bottomnav; //bottom navigation button leading to Tracker.java

    ImageButton settings_bottomnav; //bottom navigation button leading to Settings.java
    //end
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_requests_lists);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}