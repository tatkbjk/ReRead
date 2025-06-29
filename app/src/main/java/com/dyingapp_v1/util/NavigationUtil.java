package com.dyingapp_v1.util;

import android.content.Context;
import android.content.Intent;

import com.dyingapp_v1.activity.ManageAccountActivity;

public class NavigationUtil {

    public static void navigateToManageAccount(Context context) {
        Intent intent = new Intent(context, ManageAccountActivity.class);
        context.startActivity(intent);
    }
}
