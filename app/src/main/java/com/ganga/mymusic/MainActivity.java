package com.ganga.mymusic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    //PERMISSION REQUEST CONSTANT,ASSIGN ANY VALUE
    private static final int STORAGE_PERMISSION_CODE=100;
    private static final String TAG="PERMISSION_TAG";


    TextView noMusicTextView;
    RecyclerView recyclerView;

    ArrayList<AudioModel> songsList = new ArrayList<AudioModel>() ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recycler_view);
        noMusicTextView=findViewById(R.id.no_songs_text);

//        requestPermission();
        if(!checkPermission()) {
            requestPermission();
        }
        else {
            Toast.makeText(MainActivity.this, "Manage External Storage Permission allowed", Toast.LENGTH_SHORT).show();
        }

        String[] projection =
                {
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DURATION

                };
        String selection = MediaStore.Audio.Media.IS_MUSIC +"!=0";
//get all musiclistt from database after permission
      Cursor cursor= getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);
        while(cursor.moveToNext())
        {
            AudioModel songData=new AudioModel(cursor.getString(1),cursor.getString(2),cursor.getString(0));
            if(new File(songData.getPath()).exists())
            {
                songsList.add(songData);
            }
        }




            // Define the projection to get the columns you need
      /*      String[] projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DATA
            };

            // Query the media store
            Cursor cursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,  // No selection criteria
                    null,  // No selection arguments
                    null   // Default sort order
            );

            if (cursor != null) {
                try {
//                    ArrayList<AudioModel> songsList = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                        AudioModel songData = new AudioModel(title, artist, path);

                        // Check if the file exists
                        if (new File(songData.getPath()).exists()) {
                            songsList.add(songData);
                        }
                    }
                    // Handle the songsList as needed
                } finally {
                    cursor.close();
                }
            }

         */
            if ((songsList.size()==0))
            {
                noMusicTextView.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter( new MusicListAdapter(songsList,getApplicationContext()));
            }


    }

    private void requestPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
        {
            //Android is R or above
            try {
                Log.d(TAG,"requestPermission :try ");

                Intent intent=new Intent();
                intent.setAction((Settings.ACTION_SOUND_SETTINGS));
                intent.setAction(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);



                Uri uri=Uri.fromParts("package",this.getPackageName(),null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            }
            catch (Exception e)
            {
                Log.e(TAG,"requestPermission:catch",e);
                Intent intent=new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);

            }

        }
        else {
            //Android is below R
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }
    private final ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG,"onActivityResult: ");
                    //here we will handle result of our intent
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
                    {
                        //Android is 11(R) or Above
                        if (Environment.isExternalStorageManager())
                        {
                            //Manage External permission Granted
                            Log.d(TAG ,"onActivityREsult: Manage External Storage Permission is granted");

                        }
                        else
                        {
                            Log.d(TAG ,"onActivityREsult: Manage External Storage Permission is granted");
                            Toast.makeText(MainActivity.this, "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        //android is below R


                    }

                }
            }

    );

    public boolean checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
        {
            //Android is 11(R) or Above
            return Environment.isExternalStorageManager();

        }
        else {
            //android is below R
            int write = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED &&  read == PackageManager.PERMISSION_GRANTED;

        }

    }
//Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0) {
                //check each permssion if granted or not
                boolean write = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if(write&&read) {
                    //External Storage permission
                    Log.d(TAG,"onRequestPermissionResult :External Storage permission granted");


                }
                else {

                    Log.d(TAG,"onRequestPermissionResult : ");
                    Toast.makeText(this, "External Storage permission granted", Toast.LENGTH_SHORT).show();

            }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerView!=null)
        {
            recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()) );
        }
    }
}