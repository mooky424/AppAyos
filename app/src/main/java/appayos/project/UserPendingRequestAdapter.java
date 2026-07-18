package appayos.project;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class UserPendingRequestAdapter
        extends RealmRecyclerViewAdapter<Request, UserPendingRequestAdapter.ViewHolder> {
    private final UserPendingRequestList activity;
    private final Realm realm;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView requestImage;
        TextView requestTitle;
        Button completeRequest;

        public ViewHolder(View itemView) {
            super(itemView);
            requestImage = itemView.findViewById(R.id.requestImage);
            requestTitle = itemView.findViewById(R.id.requestTitle_prefilled);
            completeRequest = itemView.findViewById(R.id.completeRequest_btn);
        }
    }

    public UserPendingRequestAdapter(UserPendingRequestList activity, Realm realm,
                                     OrderedRealmCollection<Request> data) {
        super(data, true);
        this.activity = activity;
        this.realm = realm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.user_pending_request_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = getItem(position);
        holder.requestTitle.setText(request.getTitle());

        Photo photo = realm.where(Photo.class)
                .equalTo("request", request.getUuid())
                .equalTo("type", Photo.TYPES[0])
                .findFirst();
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

        holder.completeRequest.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TechnicianRequestComplete.class);
            intent.putExtra("REQUEST_UUID", request.getUuid());
            activity.startActivity(intent);
        });
    }
}
