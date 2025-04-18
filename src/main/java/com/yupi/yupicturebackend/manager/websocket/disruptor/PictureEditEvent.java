package com.yupi.yupicturebackend.manager.websocket.disruptor;

import com.yupi.yupicturebackend.manager.websocket.model.PictureEditRequestMessage;
import com.yupi.yupicturebackend.model.entity.User;
import org.springframework.web.socket.WebSocketSession;
import lombok.Data;

/**
 * 图片编辑事件
 */
@Data
public class PictureEditEvent {

    /**
     * 消息
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * 当前用户的session
     */
    private WebSocketSession session;

    /**
     * 用户
     */
    private User user;

    /**
     * 图片id
     */
    private Long pictureId;
}
