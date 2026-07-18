package appayos.project;

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

public class TrackerRequestAdapter
        extends RealmRecyclerViewAdapter<Request, TrackerRequestAdapter.ViewHolder> {
    private final Tracker activity;
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

    public TrackerRequestAdapter(Tracker activity, Realm realm,
                                 OrderedRealmCollection<Request> data) {
        super(data, true);
        this.activity = activity;
        this.realm = realm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.user_requests_rows, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = getItem(position);
        holder.requestTitle.setText(request.getTitle() + "\nTap to accept");

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

        holder.itemView.setOnClickListener(view ->
                activity.acceptRequest(request.getUuid()));
    }
}
