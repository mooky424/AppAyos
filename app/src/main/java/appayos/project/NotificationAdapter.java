package appayos.project;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class NotificationAdapter extends RealmRecyclerViewAdapter<Notification, NotificationAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textTime;

        public ViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.notification_message);
            textTime = itemView.findViewById(R.id.notification_time);
        }
    }

    Context context;

    public NotificationAdapter(Context context, OrderedRealmCollection<Notification> data) {
        super(data, true);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notification_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = getItem(position);

        holder.textMessage.setText(notification.getMessage());

        Date createdAt = notification.getCreatedAt();
        if (createdAt != null) {
            holder.textTime.setText(
                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(createdAt));
        } else {
            holder.textTime.setText("");
        }

        holder.textMessage.setTypeface(null, notification.isRead() ? Typeface.NORMAL : Typeface.BOLD);
    }
}
