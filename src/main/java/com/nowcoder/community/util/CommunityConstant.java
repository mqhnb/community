package com.nowcoder.community.util;

public interface CommunityConstant {
    int ACTIVATION_SUCCESS = 0; //重复激活
    int ACTIVATION_REPAEAT = 1; //重复激活
    int ACTIVATION_FAILED = 2; //重复激活

    int DEFAULT_EXPIRED_SECONDS = 3600 * 12; //默认登录状态

    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100; //默认登录状态

    // 帖子
    int ENTITY_TYPE_POST =1;

    //评论
    int ENTITY_TYPE_COMMENT =2;

}
