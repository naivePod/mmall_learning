package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String USER_NAME = "username";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
    public static final String PARENT_ID = "parentId";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String CATEGORY_ID = "categoryId";

    public interface Cart {
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
        int CHECKED = 1;
        int UN_CHECKED = 0;
    }
    public interface OrderBy {
        Set PRICE_DESC_ASC = Sets.newHashSet("price_asc","price_desc");
    }

    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    public enum ProductStatus {
        ON_SALE(1, "在线");

        private final int status;
        private final String desc;

        ProductStatus(int status, String desc) {
            this.status = status;
            this.desc = desc;
        }

        public int getStatus() {
            return status;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum OrderStatusEnum {
        CANCELED(0, "支付已取消"),
        NO_PAID(1, "未成功"),
        PAID(10, "已付款"),
        SHIPPED(20, "已发货"),
        ORDER_SUCCESS(30, "订单完成"),
        ORDER_CLOSED(40, "订单关闭");



        private int code;
        private String value;

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public interface AlipayCallback{
        String TRADE_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_CLOSED = "TRADE_CLOSED";
        String TRADE_SUCCESS = "TRADE_SUCCESS";
        String TRADE_FAILED = "TRADE_FINISHED";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAIL = "fail";
    }

    public enum PayPlatform{
        ZHIFUBAO(1, "支付宝");

        private int code;
        private String value;

        PayPlatform(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
