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
}
