package com.tandev.locket.test;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.tandev.locket.model.moment.Overlay;
import com.tandev.locket.test.Converters;

import java.util.List;

@Entity(tableName = "moment_table")
@TypeConverters({Converters.class}) // Sử dụng để chuyển đổi List<Overlay> sang String và ngược lại
public class MomentEntity {

    @PrimaryKey
    @NonNull
    private String canonicalUid;
    private String user;
    private String thumbnailUrl;
    private long dateSeconds; // lưu trường _seconds của đối tượng Date
    private String caption;
    private String md5;
    private List<Overlay> overlays;

    // Constructor
    public MomentEntity(@NonNull String canonicalUid, String user, String thumbnailUrl, long dateSeconds, String caption, String md5, List<Overlay> overlays) {
        this.canonicalUid = canonicalUid;
        this.user = user;
        this.thumbnailUrl = thumbnailUrl;
        this.dateSeconds = dateSeconds;
        this.caption = caption;
        this.md5 = md5;
        this.overlays = overlays;
    }

    // Getter và Setter
    @NonNull
    public String getCanonicalUid() {
        return canonicalUid;
    }

    public void setCanonicalUid(@NonNull String canonicalUid) {
        this.canonicalUid = canonicalUid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public long getDateSeconds() {
        return dateSeconds;
    }

    public void setDateSeconds(long dateSeconds) {
        this.dateSeconds = dateSeconds;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public List<Overlay> getOverlays() {
        return overlays;
    }

    public void setOverlays(List<Overlay> overlays) {
        this.overlays = overlays;
    }
}