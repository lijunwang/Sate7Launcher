package com.sate7.sate7launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPreferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("aaaa","action="+action);
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {

            sharedPreferences = context.getSharedPreferences("first_boot", Context.MODE_PRIVATE);
            boolean first_boot= sharedPreferences.getBoolean("first_boot", true);
            if (first_boot) {
                Intent activityIntent = new Intent();
                activityIntent.setClassName("com.sate7.sate7launcher", "com.sate7.sate7launcher.CopyService");
                context.startService(activityIntent);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("first_boot", false);
            editor.commit();
            Intent i = new Intent(context, LocksScreenActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else if(Intent.ACTION_SCREEN_OFF.equals(action)||Intent.ACTION_SCREEN_ON.equals(action)){
            Intent i1 = new Intent(context, LocksScreenActivity.class);
            i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i1);
        }

    }
}
