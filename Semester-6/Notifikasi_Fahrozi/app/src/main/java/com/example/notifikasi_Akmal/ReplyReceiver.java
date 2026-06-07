package com.example.notifikasi_Akmal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.core.app.RemoteInput;

public class ReplyReceiver extends BroadcastReceiver {
    private static final String KEY_TEXT_REPLY = "key_text_reply";

    @Override
    public void onReceive(Context context, Intent intent) {
        CharSequence replyText = RemoteInput.getResultsFromIntent(intent).getCharSequence(KEY_TEXT_REPLY);
        if (replyText != null) {
            String message = context.getString(R.string.toast_reply_received, replyText);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            // Proses balasan di sini misal kirim ke server
        }
    }
}
