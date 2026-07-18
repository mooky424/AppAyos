package appayos.project;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Notification extends RealmObject {
    public static final String TYPE_ACCEPTED = "accepted";
    public static final String TYPE_COMPLETED = "completed";

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private String recipient;
    private String message;
    private String relatedRequest;
    private String type;
    private Date createdAt;
    private boolean isRead;

    public Notification() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRelatedRequest() {
        return relatedRequest;
    }

    public void setRelatedRequest(String relatedRequest) {
        this.relatedRequest = relatedRequest;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "uuid='" + uuid + '\'' +
                ", recipient='" + recipient + '\'' +
                ", message='" + message + '\'' +
                ", relatedRequest='" + relatedRequest + '\'' +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
                ", isRead=" + isRead +
                '}';
    }
}
