package com.rishi.ssangeet;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class CommonMethod {
    public static MediaPlayer player = null;
    public static void SoundPlayer(Context ctx, Uri id){
        player = MediaPlayer.create(ctx, id);
    }
}
