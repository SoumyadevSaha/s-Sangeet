package com.rishi.ssangeet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String MSG1 = "DeveloperSoumya.sSangeet.MSG1";
    public static final String MSG2 = "DeveloperSoumya.sSangeet.MSG2";
    ListView listView;
    String[] items; //To store the names of the songs in our device.
    //To access the phone-data, we need to ask for permission in our onCreate method. For that goto AndroidManifests.xml file.
    ArrayList<File> songList = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewSong);
        runtimePermission();
    }
    public void PlayMusic(View v)
    {
//        TextView txt = v.findViewById(R.id.txtSongName);
//        String message = txt.getText().toString();
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, myMusicPlayer.class);
        TextView txt = v.findViewById(R.id.txtSongName);
        String message = txt.getText().toString();
        intent.putExtra(MSG1, message);
        ArrayList<File> mySongs2 = findSong(Environment.getExternalStoragePublicDirectory("music"));
        intent.putExtra(MSG2, mySongs2);
        startActivity(intent);
    }
    public void runtimePermission() // Method to take run-time permission
    {
        //Copy the dependency for dexter library from git hub and paste it in gradle scripts. (app labelled gradle file.)
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
//            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                try {
                    displaySongs();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public ArrayList<File> findSong (File file) //Method to create array list of the files containing songs.
    {
        File files [] = file.listFiles();
        for (File f : files)
        {
            Log.d("File_names ", f.getName().toString());
            if (f.isDirectory())
            {
                Log.d("folder_name : ", f.getName().toString());
                songList.addAll(findSong(f));
            }
            else
            {
//                Log.d("enters_this folder ", f.getName().toString());
                if (f.getName().endsWith(".mp3") || f.getName().endsWith(".wav") || f.getName().endsWith(".m4a"))
//                    Log.d("Song_name ", f.getName());
                    songList.add(f);
            }
        }
        return songList;
    }

//    @RequiresApi(api = Build.VERSION_CODES.S)
    void displaySongs () throws InterruptedException {
        String storagePath = System.getenv("SECONDARY_STORAGE");
        File sdcard;
        if (storagePath != null) {
            String[] storagePathArray = storagePath.split(":");
            sdcard = new File(storagePathArray[0]);
        }
        else {
//            sdcard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            sdcard = new File("/storage/emulated/0");
        }
//        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        Log.d("ExternalStorage1", sdcard.getName());
        ArrayList<File> mySongs = new ArrayList<File>();
        try {
            mySongs = findSong(sdcard);
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

        if (mySongs.size() == 0) {
            sdcard = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());
            Log.d("ExternalStorage2", sdcard.getName());
            try {
                mySongs = findSong(sdcard);
            }
            catch(NullPointerException e){
                e.printStackTrace();
            }
            if (mySongs.size() == 0) {
                items = new String[0];
                Toast.makeText(this, "No mp3, wav or m4a file format found !!", Toast.LENGTH_LONG).show();
            }
            else{
                items = new String[mySongs.size()];
                int i = 0;
                for (File f : mySongs) {
                    items[i] = f.getName().toString();
                    i++;
                }
            }
        }
        else {
            items = new String[mySongs.size()];
            int i = 0;
            for (File f : mySongs) {
                items[i] = f.getName().toString();
                i++;
            }
        }
        // Attaching all the items in our list view ->
//        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
//        listView.setAdapter(myAdapter);
        customAdapter cust = new customAdapter();
        listView.setAdapter(cust);
    }

    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View myview = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myview.findViewById(R.id.txtSongName);
            textsong.setSelected(true);
            textsong.setText(items[i]);

            return myview;
        }
    }
}