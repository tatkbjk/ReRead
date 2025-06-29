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


    public static String getUserUpdateUrl(String userId) {
        return BASE_URL + "/user/update/" + userId;

    }

    public static String getBookInfoUrl(int bookInfoId) {
        return BASE_URL + "/bookinfo/" + bookInfoId;
    }

    public static String getAllBookstockUrl(int bookInfoId) {
        return BASE_URL + "/bookstocks/bookinfo/" + bookInfoId;
    }

    public static final String CART_ADD_URL = BASE_URL + "/cart/add";
    public static String getCartUrl(String userId) {
        return BASE_URL + "/cart/" + userId;
    }

    public static String getBookISBNUrl(String bookISBN_n) {
        return BASE_URL + "/bookstocks/isbn/" + bookISBN_n;
    }

    public static String getDeleteAllCartUrl(String userId) {
        return BASE_URL + "/cart/clear/" + userId;
    }

    public static String getDeleteCartItemUrl(String userId, String isbn) {
        return BASE_URL + "/cart/item/" + userId + "/" + isbn;
    }

    public static String getCheckVoucherUrl() {
        return BASE_URL + "/check-voucher";
    }

    public static String getWishlistUrl(String userId) {
        return BASE_URL + "/wishlist/" + userId;
    }


    public static String getBasicBookInfoUrl(int bookInfoId) {
        return BASE_URL + "/bookinfo/basic/" + bookInfoId;
    }

    public static final String WISHLIST_ADD_URL = BASE_URL + "/wishlist/add";

    public static String getClearWishlistUrl(String userId) {
        return BASE_URL + "/wishlist/clear/" + userId;
    }

    public static String getDeleteWishlistItemUrl(String userId, String bookInfoId) {
        return BASE_URL + "/wishlist/item/" + userId + "/" + bookInfoId;
    }


//    public static final String FLASK_BASE_URL = "http://192.168.1.91:5000";

    public static final String FLASK_BASE_URL = "http://10.0.2.2:5000";

    public static String getRecommendations(String userId, String bookisbn_n) {
        String url = FLASK_BASE_URL + "/recommend?";
        if (userId != null && !userId.isEmpty()) {
            url += "user_id=" + userId;
        }
        if (bookisbn_n != null && !bookisbn_n.isEmpty()) {
            if (!url.endsWith("?")) url += "&";
            url += "bookisbn_n=" + bookisbn_n;
        }
        return url;
    }

    public static String getSearchBooksUrl(String query) {
        return BASE_URL + "/search?query=" + query;
    }

    public static final String CREATE_QUOTATION_URL = BASE_URL + "/quotations";
    public static final String GET_QUOTATIONS_URL = BASE_URL + "/quotations";
    public static final String DELETE_QUOTATION_URL = BASE_URL + "/quotations";
    public static final String UPDATE_QUOTATION_STATUS_URL = BASE_URL + "/quotations";


    public static String getEventsByUserId(String userId) {
        return BASE_URL + "/events/user/" + userId;
    }
    public static String getOrdersByUserId(String userId) {
        return BASE_URL + "/orders/user/" + userId;
    }

    public static final String AUTHORS_URL = BASE_URL + "/authors";



    public static String getAuthorDetailsUrl(String authorId) {
        return BASE_URL + "/authors/" + authorId;
    }


    public static String getBooksByAuthorUrl(String authorName) {
        // Chú ý encode tên tác giả để xử lý các ký tự đặc biệt như khoảng trắng
        return BASE_URL + "/books/author/" + android.net.Uri.encode(authorName);
    }

    public static final String EVENT_LIST_URL = BASE_URL + "/events";
    public static final String REGISTER_EVENT_URL = BASE_URL + "/eventregistrations";
}
