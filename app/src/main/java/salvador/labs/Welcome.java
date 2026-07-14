package salvador.labs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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

public class Welcome extends AppCompatActivity {

    TextView textViewWelcome;
    ImageView imageViewIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewWelcome = findViewById(R.id.welcome);
        imageViewIcon = findViewById(R.id.imageIcon);

        SharedPreferences sharedPrefs = getSharedPreferences("Lab4", MODE_PRIVATE);
        String uuid = sharedPrefs.getString("user", "");
        Boolean rememberMe = sharedPrefs.getBoolean("rememberMe", false);
        String image = sharedPrefs.getString("image", "");

        Realm realm = Realm.getDefaultInstance();

        User user = realm.where(User.class)
                .equalTo("uuid", uuid)
                .findFirst();

        String message = "";
        if (user.getUsername() != null) {
             message = "Welcome " + user.getUsername() + "!!";
        }
        if (rememberMe) {
            message += " You will be remembered!";
        }
        textViewWelcome.setText(message);
        File imageFile = new File(image);
        if (imageFile.exists()) {
            Picasso.get()
                    .load(imageFile)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageViewIcon);
        } else {
            imageViewIcon.setImageResource(R.mipmap.ic_launcher);
        }

        realm.close();
    }
}
