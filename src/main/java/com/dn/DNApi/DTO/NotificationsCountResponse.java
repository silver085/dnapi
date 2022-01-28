package com.dn.DNApi.DTO;

public class NotificationsCountResponse extends BaseResponse {
    private String userId;
    private int unread;

    public NotificationsCountResponse(String userId, int unread) {
        this.userId = userId;
        this.unread = unread;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }
}
