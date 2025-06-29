package com.dyingapp_v1.util;

import android.content.Context;
import android.content.SharedPreferences;

public class BookSession {
    private static final String PREF_NAME = "bookSession";
    private static final String KEY_BOOK_ID = "bookId";           // Product.id
    private static final String KEY_BOOK_ISBN_N = "bookIsbn_n";   // Số định danh phụ như ISBN hoặc mã nội bộ

    // Lưu Book ID và Book ISBN_n
    public static void saveBookSession(Context context, String bookId, String bookIsbn_n) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_BOOK_ID, bookId);
        editor.putString(KEY_BOOK_ISBN_N, bookIsbn_n);
        editor.apply();
    }

    // Lấy Book ID
    public static String getBookId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_BOOK_ID, null);
    }

    // Lấy Book ISBN_n
    public static String getBookIsbn_n(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_BOOK_ISBN_N, null);
    }

    // Xóa session
    public static void clearBookSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
