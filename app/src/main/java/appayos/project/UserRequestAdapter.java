package appayos.project;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import io.realm.Sort;

public class UserRequestAdapter extends RealmRecyclerViewAdapter<Request, UserRequestAdapter.ViewHolder> {
    private final UserRequestList activity;
    private final Realm realm;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView requestImage;
        TextView requestTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            requestImage = itemView.findViewById(R.id.requestImage);
            requestTitle = itemView.findViewById(R.id.requestTitle_prefilled);
        }
    }

    public UserRequestAdapter(UserRequestList activity, Realm realm,
                              OrderedRealmCollection<Request> data) {
        super(data, true);
        this.activity = activity;
        this.realm = realm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.request_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = getItem(position);
        String status = request.getStatus();
        if (status != null && !status.isEmpty()) {
            status = status.substring(0, 1).toUpperCase() + status.substring(1);
        }
        holder.requestTitle.setText(request.getTitle() + "\n"
                + request.getDescription() + "\nStatus: " + status);

        Photo photo = realm.where(Photo.class)
                .equalTo("request", request.getUuid())
                .equalTo("type", Photo.TYPES[0])
                .sort("createdAt", Sort.DESCENDING)
                .findFirst();
        Picasso.get().cancelRequest(holder.requestImage);
        if (photo != null && photo.getPath() != null) {
            File imageFile = new File(photo.getPath());
            Picasso.get()
                    .load(imageFile)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.requestImage);
        } else {
            holder.requestImage.setImageResource(R.mipmap.ic_launcher);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, UserRequestEdit.class);
            intent.putExtra("REQUEST_UUID", request.getUuid());
            activity.startActivity(intent);
        });
    }
}
