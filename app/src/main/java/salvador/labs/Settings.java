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
import androidx.recyclerview.widget.RecyclerView;

public class Settings extends AppCompatActivity {

    //for final proj
    Button SeeAllUsers; //leads to UsersList.java

    RecyclerView RecyclerViewOFUsernames; //RecyclerView of just usernames (layout is usernames_rows.xml)

    EditText changeUsername_input; //input box where user can change their username

    EditText changePassword_input; //input box where user can change their password

    ImageButton home_bottomnav; //bottom navigation button leading to UserRequestsList.java

    ImageButton tracker_bottomnav; //bottom navigation button leading to Tracker.java

    ImageButton settings_bottomnav; //bottom navigation button leading to Settings.java
    //end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}