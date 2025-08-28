package com.vietnguyen.ums.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(schema = "ums", name = "user_info")
public class UserInfoEntity {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "bio")
    private String bio;

    @Column(name = "address")
    private String address;

    @Column(name = "is_display_name_public")
    private Boolean isDisplayNamePublic;

    @Column(name = "is_avatar_public")
    private Boolean isAvatarPublic;

    @Column(name = "is_bio_public")
    private Boolean isBioPublic;

    @Column(name = "is_address_public")
    private Boolean isAddressPublic;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Boolean getIsDisplayNamePublic() { return isDisplayNamePublic; }
    public void setIsDisplayNamePublic(Boolean isDisplayNamePublic) { this.isDisplayNamePublic = isDisplayNamePublic; }

    public Boolean getIsAvatarPublic() { return isAvatarPublic; }
    public void setIsAvatarPublic(Boolean isAvatarPublic) { this.isAvatarPublic = isAvatarPublic; }

    public Boolean getIsBioPublic() { return isBioPublic; }
    public void setIsBioPublic(Boolean isBioPublic) { this.isBioPublic = isBioPublic; }

    public Boolean getIsAddressPublic() { return isAddressPublic; }
    public void setIsAddressPublic(Boolean isAddressPublic) { this.isAddressPublic = isAddressPublic; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

