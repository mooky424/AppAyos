package salvador.labs;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class UserAdapter extends RealmRecyclerViewAdapter<User, UserAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername;
        TextView textPassword;
        ImageView imageIcon;
//        ImageButton buttonEdit;
//        ImageButton buttonDelete;


        public ViewHolder(View itemView) {
            super(itemView);

            textUsername = itemView.findViewById(R.id.username);
            textPassword = itemView.findViewById(R.id.password);
            imageIcon = itemView.findViewById(R.id.imageIcon);
//            buttonEdit = itemView.findViewById(R.id.edit);
//            buttonDelete = itemView.findViewById(R.id.delete);
        }
    }

    Admin activity;
    public UserAdapter(Admin activity, OrderedRealmCollection<User> data) {
        super(data, true);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.row_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = getItem(position);
        String userUuid = user.getUuid();

        holder.textUsername.setText(user.getUsername());
        holder.textPassword.setText(user.getPassword());
        String image = user.getImage();
        File imageFile = new File(image);
        if (imageFile.exists()) {
            Picasso.get()
                    .load(imageFile)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.imageIcon);
        } else {
            holder.imageIcon.setImageResource(R.mipmap.ic_launcher);
        }
//        holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(activity, Edit.class);
//                intent.putExtra("UUID", userUuid);
//                activity.startActivity(intent);
//            }
//        });
//
//        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activity.realm.executeTransactionAsync(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();
//                        user.deleteFromRealm();
//                    }
//                });
//            }
//        });
    }


}
