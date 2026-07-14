package salvador.labs;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Photo extends RealmObject {
    public static final String[] TYPES = {
            "request",
            "completion",
            "user"
    };

    @PrimaryKey
    private String uuid;
    private String uploader;
    private String path;
    private Date createdAt;
    private String type;

    public Photo() {

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (Arrays.asList(TYPES).contains(type.toLowerCase())) {
            throw new IllegalArgumentException("type must be request, completion, or user");
        }
        this.type = type;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "uuid='" + uuid + '\'' +
                ", uploader='" + uploader + '\'' +
                ", path='" + path + '\'' +
                ", createdAt=" + createdAt +
                ", type='" + type + '\'' +
                '}';
    }
}
