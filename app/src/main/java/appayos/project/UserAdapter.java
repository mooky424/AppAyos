package appayos.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class UserAdapter extends RealmRecyclerViewAdapter<User, UserAdapter.ViewHolder> {
    public interface Listener {
        void onEdit(String userUuid);
    }

    private final Context context;
    private final Listener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername;
        TextView textRole;

        public ViewHolder(View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.username_ofUser);
            textRole = itemView.findViewById(R.id.role_ofUser);
        }
    }

    public UserAdapter(Context context, OrderedRealmCollection<User> data, Listener listener) {
        super(data, true);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = getItem(position);
        holder.textUsername.setText(user.getUsername());
        String role;
        if (user.isAdmin() && user.isTechnician()) {
            role = "Admin, Technician";
        } else if (user.isAdmin()) {
            role = "Admin";
        } else if (user.isTechnician()) {
            role = "Technician";
        } else {
            role = "Member";
        }
        holder.textRole.setText(role);
        holder.itemView.setOnClickListener(view -> listener.onEdit(user.getUuid()));
    }
}
