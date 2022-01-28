package com.dn.DNApi.DTO;

import com.dn.DNApi.Domain.Notification;
import org.springframework.data.domain.Page;

public class NotificationPageDTO extends BaseResponse{
    Page<Notification> page;

    public NotificationPageDTO(Page<Notification> page){
        this.page = page ;
    }

    public Page<Notification> getPage() {
        return page;
    }

    public void setPage(Page<Notification> page) {
        this.page = page;
    }
}
