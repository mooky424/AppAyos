package appayos.project;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Request extends RealmObject {
    public static final String[] STATUSES = {
            "posted",
            "accepted",
            "completed"
    };

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private String user;
    private String title;
    private String description;
    private String status;
    private Date createdAt;
    private String acceptedBy;
    private String statusDescription;

    public Request() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || !Arrays.asList(STATUSES).contains(status.toLowerCase())) {
            throw new IllegalArgumentException("status must be posted, accepted, or completed");
        }
        this.status = status.toLowerCase();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAcceptedBy() {
        return acceptedBy;
    }

    public void setAcceptedBy(String acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @Override
    public String toString() {
        return "Request{" +
                "uuid='" + uuid + '\'' +
                ", user='" + user + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", acceptedBy='" + acceptedBy + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                '}';
    }
}
