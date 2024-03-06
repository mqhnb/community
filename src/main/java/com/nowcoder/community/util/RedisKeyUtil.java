package com.nowcoder.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTIYT_LIKE = "like:entity";

    public static String getEntityLikeKey(int entityType,int entityId)
    {
        return PREFIX_ENTIYT_LIKE + SPLIT + entityType +SPLIT +entityId;
    }
}
