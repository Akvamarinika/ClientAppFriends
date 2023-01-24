package com.akvamarin.clientappfriends.receivers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public class InternetReceiver extends BroadcastReceiver {
    private static final String TAG = "Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = CheckInternet.getNetworkInfo(context);

        if (status.equalsIgnoreCase("connected")){
            Log.d(TAG, "onReceive: CONNECTED" );
            String networkStatus = "Соединение восстановлено";
            //createDialog(networkStatus, context);
        } else {
            Log.d(TAG, "onReceive: NOT connected" );
            String networkStatus = "Отсутствует подключение к Интернету";
            //createDialog(networkStatus, context);
        }
    }

    private void  createDialog(String networkStatus, Context context){
       AlertDialog.Builder builder = new AlertDialog.Builder(context);
       builder.setTitle("Состояние сети Интернет");
       builder.setMessage(networkStatus);
       builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();

           }
         });

         builder.create().show();
    }
}
