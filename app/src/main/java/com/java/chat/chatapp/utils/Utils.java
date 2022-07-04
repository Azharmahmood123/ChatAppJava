package com.java.chat.chatapp.utils;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.java.chat.chatapp.App;
import com.java.chat.chatapp.data.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static boolean isEmailValid(String email) {
        return TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return TextUtils.isEmpty(password) || password.length() <= 5;
    }

    public static boolean isDisplayNameValid(String displayName) {
        return TextUtils.isEmpty(displayName) || displayName.length() <= 2;
    }

    public static boolean isPhoneValid(String phone) {
        return TextUtils.isEmpty(phone) || phone.length() <= 5;
    }

    public static void showToast(String message) {
        Toast toast = Toast.makeText(App.self, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) view = new View(activity);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public static String getTimeAgoFormat(long timestamp) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(timestamp).toString();
    }

    @SuppressLint("SimpleDateFormat")
    public static void bindEpochTimeMsToDate(TextView textView, Long epochTimeMs) {
        long currentTimeMs = new Date().getTime();
        long numOfDays = TimeUnit.MILLISECONDS.toDays(currentTimeMs - epochTimeMs);
        String replacePattern = numOfDays >= 1L ? "Yy" : "YyMd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(replacePattern);
        String formattedText = simpleDateFormat.format(new Date(epochTimeMs));
        textView.setText(formattedText);
    }

    public static void bindShouldMessageShowTimeText(TextView textView, Message message, List<Message> currentList) {
        int halfHourInMilli = 1800000;
        int index = currentList.indexOf(message);
        if (index == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            Message messageBefore = currentList.get(index - 1);
            if (abs(messageBefore.epochTimeMs - message.epochTimeMs) > halfHourInMilli) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }
}
