package mytune.dev.v.whatsapptune;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.support.v7.graphics.Palette;

import java.net.URISyntaxException;


public class MainActivity extends ActionBarActivity {
    final static int RQS_OPEN_AUDIO_MP3 = 2;
    public String filename;
    public String fn=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //show
        MediaMetadataRetriever metaRetriver;
        byte[] art;
        final ImageView album_art = (ImageView) findViewById(R.id.album_art);

        //core
        TextView tv4 = (TextView) findViewById(R.id.Text4);
        SharedPreferences sharedPref = getSharedPreferences("whatsapptune", Context.MODE_MULTI_PROCESS);
        fn = sharedPref.getString("filename", "Select a MP3 File!");
        if(!(fn=="k") || fn!=null){tv4.setText(fn);}
        final SharedPreferences.Editor editor = sharedPref.edit();


        //Checking status of preferencs in menu
        final Switch repeatChkBx = (Switch) findViewById(R.id.switch1);
        TextView tv2 = (TextView) findViewById(R.id.Text2);
        if (sharedPref.getInt("NActive", 0) == 1) {
            repeatChkBx.setChecked(true);
            tv2.setText("WhatsAppTune is Enabled");
        } else {
            repeatChkBx.setChecked(false);
            tv2.setText("WhatsAppTune is Disabled");
        }
        //Mytune Enabling listener
        repeatChkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView tv2 = (TextView) findViewById(R.id.Text2);
                if (fn == null) {
                    //  Toast.makeText(MainActivity.this,"Please select a MP3 file above",Toast.LENGTH_LONG);
                }
                if (isChecked) {

                    editor.putInt("NActive",1);
                    startService( new Intent(MainActivity.this, NotificationService.class));
                    editor.apply();
                    tv2.setText("WhatsAppTune is Enabled");
                } else {
                    editor.putInt("NActive",0);
                    stopService(new Intent(MainActivity.this, NotificationService.class));
                    editor.apply();
                    tv2.setText("WhatsAppTune is Disabled");
                }

            }
        });


        TextView tv3 = (TextView) findViewById(R.id.Text3);
        tv3.setOnTouchListener(new View.OnTouchListener()

                               {


                                   public boolean onTouch(View v, MotionEvent event) {
                                       // TODO Auto-generated method stub


                                       //do stuff here
                                       Intent intent = new Intent();
                                       intent.setType("audio/mpeg");
                                       intent.setAction(Intent.ACTION_GET_CONTENT);
                                       startActivityForResult(Intent.createChooser(
                                               intent, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);


                                       return false;
                                   }
                               }

        );
        if (fn != "Select a MP3 File!") {
            try {


                metaRetriver = new MediaMetadataRetriever();
                metaRetriver.setDataSource(fn);
                art = metaRetriver.getEmbeddedPicture();
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                album_art.setImageBitmap(songImage);
                Blur b=new Blur();
                Bitmap blursongimage=b.fastblur(MainActivity.this,songImage,10);
                ImageView bluimg=(ImageView)findViewById(R.id.bluimg);
                bluimg.setImageBitmap(blursongimage);

            } catch (Exception e) {
                e.printStackTrace();
                album_art.setBackgroundColor(Color.GRAY);
                RelativeLayout rl1 = (RelativeLayout) findViewById(R.id.rl1);
                rl1.setBackgroundColor(Color.GRAY);
            }
        }else{
            int id = getResources().getIdentifier("mytune.dev.v.whatsapptune:drawable/" + "defaultcover", null, null);
            album_art.setImageResource(id);
            RelativeLayout rl1 = (RelativeLayout) findViewById(R.id.rl1);
            rl1.setBackgroundColor(getResources().getColor(R.color.primaryDef));

        }
      //  LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

    }

/*
    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");



            TableRow tr = new TableRow(getApplicationContext());
            tr.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView textview = new TextView(getApplicationContext());
            textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f));
            textview.setTextSize(20);
            textview.setTextColor(Color.parseColor("#0B0719"));
            textview.setText(Html.fromHtml(pack +"<br><b>" + title + " : </b>" + text));
            tr.addView(textview);
            tab.addView(tr);




        }
    };*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) if (requestCode == RQS_OPEN_AUDIO_MP3) {

            Uri audioFileUri = data.getData();
            try {
                String path = getPath(this, audioFileUri);
                Log.i("Path",path);
                TextView tv4 = (TextView) findViewById(R.id.Text4);
                SharedPreferences sharedPref = getSharedPreferences("whatsapptune", Context.MODE_MULTI_PROCESS);
                final SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("filename", path);
                editor.apply();
                tv4.setText(sharedPref.getString("filename", null));
                MediaMetadataRetriever metaRetriver;
                byte[] art;
                ImageView album_art = (ImageView) findViewById(R.id.album_art);
                metaRetriver = new MediaMetadataRetriever();
                metaRetriver.setDataSource(sharedPref.getString("filename", null));
                art = metaRetriver.getEmbeddedPicture();
try{                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                album_art.setImageBitmap(songImage);
                //Background color for art
                /*    Palette.generateAsync(songImage, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            // Here's your generated palette
                            Palette.Swatch swatch = palette.getVibrantSwatch();
                            if (swatch != null) {
                                RelativeLayout rl1 = (RelativeLayout) findViewById(R.id.rl1);
                                rl1.setBackgroundColor(swatch.getRgb());
                                //  titleView.setTextColor(swatch.getTitleTextColor());
                            }
                        }
                    });*/
                Blur b=new Blur();
                Bitmap blursongimage=b.fastblur(MainActivity.this,songImage,10);
                ImageView bluimg=(ImageView)findViewById(R.id.bluimg);
                bluimg.setImageBitmap(blursongimage);
}catch(Exception e){e.printStackTrace();album_art.setBackgroundColor(getResources().getColor(R.color.primaryDef));}
            } catch (Exception e) {
                e.printStackTrace();
                ImageView album_art = (ImageView) findViewById(R.id.album_art);
                album_art.setBackgroundColor(Color.GRAY);
            }

        }
    }


    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

}