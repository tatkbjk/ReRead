package com.dyingapp_v1.connector;

public class ApiConnector {
    public static final String BASE_URL = "http://10.0.2.2:3000"; // For Android emulator
    public static final String REGISTER_URL = BASE_URL + "/register";
    public static final String LOGIN_URL = BASE_URL + "/login";
    public static final String FIND_EMAIL_URL = BASE_URL + "/findEmail";
    public static final String RESET_PASSWORD_URL = BASE_URL + "/resetPassword";
    public static final String FLASH_SALE_URL = BASE_URL + "/flashsale";
    public static final String BEST_SELLERS_URL = BASE_URL + "/bestsellers";
    public static String getUserInfoUrl(String userId) {
        return BASE_URL + "/user/" + userId;
    }

    public static String getBookInfoUrl(int bookInfoId) {
        return BASE_URL + "/bookinfo/" + bookInfoId;
    }

    public static String getAllBookstockUrl(int bookInfoId) {
        return BASE_URL + "/bookstocks/bookinfo/" + bookInfoId;
    }


    public static String getUserUpdateUrl(String userId) {
        return BASE_URL + "/user/update/" + userId;

    }
}
