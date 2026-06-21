package com.family.points.common;

/**
 * 系统常量类
 */
public class Constants {

    /**
     * 成员类型
     */
    public static final String MEMBER_TYPE_PARENT = "PARENT";
    public static final String MEMBER_TYPE_CHILD = "CHILD";

    /**
     * 规则类型
     */
    public static final String RULE_TYPE_ADD = "ADD";
    public static final String RULE_TYPE_DEDUCT = "DEDUCT";

    /**
     * 适用对象
     */
    public static final String APPLY_TO_PARENT = "PARENT";
    public static final String APPLY_TO_CHILD = "CHILD";
    public static final String APPLY_TO_ALL = "ALL";

    /**
     * 状态
     */
    public static final Integer STATUS_DISABLED = 0;
    public static final Integer STATUS_ENABLED = 1;

    /**
     * 兑换状态
     */
    public static final String EXCHANGE_STATUS_EXCHANGED = "EXCHANGED";
    public static final String EXCHANGE_STATUS_DELIVERED = "DELIVERED";
    public static final String EXCHANGE_STATUS_COMPLETED = "COMPLETED";

    /**
     * 系统配置键
     */
    public static final String CONFIG_KEY_POINT_TO_MONEY_RATIO = "POINT_TO_MONEY_RATIO";
    public static final String CONFIG_KEY_SYSTEM_NOTICE = "SYSTEM_NOTICE";
}
