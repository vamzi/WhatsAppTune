package mytune.dev.v.whatsapptune;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;


public class NotificationService extends NotificationListenerService {
    public static MediaPlayer wt;
    Context context;
    public static String fn=null;
    AudioManager mManager;
    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        mManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


    }
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
        SharedPreferences sharedPref = getSharedPreferences("whatsapptune", Context.MODE_MULTI_PROCESS);
        fn=sharedPref.getString("filename",null);
        int NActive=sharedPref.getInt("NActive",0);
        String pack = sbn.getPackageName();
//        String ticker = sbn.getNotification().tickerText.toString();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
      String text = extras.getCharSequence("android.text").toString();
        Log.i("Nactive",""+NActive);
        Log.i("Package",pack);
       // Log.i("Ticker", ticker);
        Log.i("Title", title);
        Log.i("Text", text);

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
       // msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        if(NActive==1) {
            if (text.equals("Outgoing call") && pack.equals("com.whatsapp")) {
                init();
                try {

                    mManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

                    wt = new MediaPlayer();
                    wt.setDataSource(NotificationService.this, Uri.parse(fn));
                    wt.prepare();
                    wt.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    wt.stop();
                    wt.release();

                }

            }
            //when call lifted
            // LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
            if (text.equals("Ongoing call") && pack.equals("com.whatsapp")) {
                try {
                    if(wt.isPlaying()){
                    wt.stop();
                    wt.release();wt=null;
                    }
                    mManager.setMode(AudioManager.MODE_NORMAL);
                    Log.i("Stopping", text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


void init(){
    SharedPreferences sharedPref = getSharedPreferences("whatsapptune",Context.MODE_MULTI_PROCESS);
    fn=sharedPref.getString("filename",null);
}

    @Override
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");
        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
       // String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();
        if(text.equals("Outgoing call") && pack.equals("com.whatsapp")){
          try{
              if(wt.isPlaying()){
                  wt.stop();
                  wt.release();wt=null;
              }
              mManager.setMode(AudioManager.MODE_NORMAL);
            Log.i("Music stop",text);
          }catch(Exception e){e.printStackTrace();}
        }
    }
}

