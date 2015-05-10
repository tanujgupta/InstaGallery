package com.tanujgupta.bellytest.instagallery.Log;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

// public  class for logging and displaying toasts

public class L {

    public static void m(String message) {
        Log.d("TANUJ", "" + message);
    }
    public static void t(Context context, String message) {

        Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();

    }
    public static void T(Context context, String message) {

        Toast.makeText(context, message + "", Toast.LENGTH_LONG).show();

    }
}