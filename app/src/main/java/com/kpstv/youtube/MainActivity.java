package com.kpstv.youtube;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kpstv.youtube.fragments.DiscoverFragment;
import com.kpstv.youtube.fragments.HistoryBottomSheet;
import com.kpstv.youtube.fragments.HistoryFragment;
import com.kpstv.youtube.fragments.LibraryFragment;
import com.kpstv.youtube.fragments.LocalMusicFragment;
import com.kpstv.youtube.fragments.LocalSearchFragment;
import com.kpstv.youtube.fragments.NCFragment;
import com.kpstv.youtube.fragments.OPlaylistFragment;
import com.kpstv.youtube.fragments.PlaylistFragment;
import com.kpstv.youtube.fragments.SFragment;
import com.kpstv.youtube.fragments.SearchFragment;
import com.kpstv.youtube.fragments.SleepBottomSheet;
import com.kpstv.youtube.models.MetaModel;
import com.kpstv.youtube.models.NPlayModel;
import com.kpstv.youtube.models.YTConfig;
import com.kpstv.youtube.receivers.SongBroadCast;
import com.kpstv.youtube.utils.HttpHandler;
import com.kpstv.youtube.utils.SpotifyTrack;
import com.kpstv.youtube.utils.YTMeta;
import com.kpstv.youtube.utils.YTStatistics;
import com.kpstv.youtube.utils.YTutils;
import com.kpstv.youtube.ytextractor.ExtractorException;
import com.kpstv.youtube.ytextractor.YoutubeStreamExtractor;
import com.kpstv.youtube.ytextractor.model.YoutubeMedia;
import com.kpstv.youtube.ytextractor.model.YoutubeMeta;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import at.huber.youtubeExtractor.Format;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import cat.ereza.customactivityoncrash.config.CaocConfig;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements AppInterface, SleepBottomSheet.ItemClickListener, HistoryBottomSheet.BottomSheetListener, NCFragment.NoConnectionListener {

    // https://www.googleapis.com/youtube/v3/videos?id=BDocp-VpCwY&key=AIzaSyBYunDr6xBmBAgyQx7IW2qc770aoYBidLw&part=snippet,statistics

    // API KEY= AIzaSyBMqerRAATEnrsfPnWYfeqDdqX0TbR0bEo

    /* https://www.googleapis.com/youtube/v3/search?part=snippet,id&q=tuto&type=video&key=AIzaSyBYunDr6xBmBAgyQx7IW2qc770aoYBidLw

    * Code to launch player
    *
    *           Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
                intent.putExtra("youtubelink",stringarray);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    *
    * */

    Fragment HistoryFrag;
    static Fragment SearchFrag;
    public static FragmentManager fragmentManager;
    public static Fragment PlaylistFrag, libraryFrag, FavouriteFrag,localMusicFrag, localSearchFrag;
    Fragment NCFrag; String ytLink;
    static SharedPreferences preferences,settingPref;
    public static LinearLayout bottom_player, adViewLayout;
    static ImageButton actionUp,actionPlay;static ProgressBar loadProgress,songProgress;
    static TextView actionTitle; static AdView adView;
    static AsyncTask<String,String,Void> LoadVideo; public static Activity activity;
    static AsyncTask<Void,Void,Void> LoadOffline;

    public static ArrayList<NPlayModel> nPlayModels;
    public static SimpleExoPlayer player;  public static boolean supportFFmpeg=false,loadedFavFrag=false;
    public static MediaSource mediaSource; private static final String TAG = "MainActivity";
    public static DefaultDataSourceFactory dataSourceFactory;
    public static DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    public static TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
    public static TrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
    public static String selectedItemText=""; public static int sleepSeconds;
    private static MediaSessionCompat mediaSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Disabling URI exposure coz I don't want to take much efforts...
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


      /*  if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }*/

        settingPref = getSharedPreferences("settings",MODE_PRIVATE);
        isEqualizerEnabled = settingPref.getBoolean("equalizer_enabled",false);

        dataSourceFactory = new DefaultDataSourceFactory(MainActivity.this,
                Util.getUserAgent(MainActivity.this,
                        getResources().getString(R.string.app_name)), BANDWIDTH_METER);

        player = ExoPlayerFactory.newSimpleInstance(MainActivity.this, trackSelector);

        activity = MainActivity.this;

        createNotification();

        ytConfigs = new ArrayList<>();
        yturls = new ArrayList<>();
        nPlayModels = new ArrayList<>();

        // Remove this code afterwards...
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        Log.e("HeightMatrix",height+"");

        // Set CrashActivity...
        CaocConfig.Builder.create()
                .errorActivity(ErrorActivity.class)
                .apply();

        //TODO: Change app unit id, Sample : ca-app-pub-3940256099942544~3347511713, ca-app-pub-1763645001743174~5602018181
        MobileAds.initialize(this, "ca-app-pub-xxx3645001743174~5602018181");

        // Get required views...
        adView = findViewById(R.id.adView);
        adViewLayout = findViewById(R.id.adViewLayout);
        bottom_player = findViewById(R.id.bottom_player);
        actionPlay = findViewById(R.id.action_play);
        actionUp = findViewById(R.id.action_maximize);
        actionTitle = findViewById(R.id.action_title);
        songProgress = findViewById(R.id.songLayoutProgress);
        loadProgress = findViewById(R.id.song_progress);

        // Check onComing links from YouTube or Spotify...
        CheckIntent(getIntent());

        String history = YTutils.readContent(this,"History");
        if (history !=null && !history.isEmpty()) {
            Log.e("historyContents",history+"");

            SharedPreferences preferences = getSharedPreferences("history",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("urls",history.replace("&#10;","").trim());
            editor.apply();
            new File(getFilesDir().toString()+"/History").delete();
        }

      //  mediaSession = new MediaSessionCompat(this,"MediaSessionCompat");

      /*  ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), SongBroadCast.class);
        mediaSession = new MediaSessionCompat(getApplicationContext(), "Tag", mediaButtonReceiver, null);
        mediaSession.setCallback(mMediaSessionCallback);
        mediaSession.setFlags( MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS );
        mediaSession.setActive(true);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, SongBroadCast.class);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(this, 0, mediaButtonIntent, 0);
        mediaSession.setMediaButtonReceiver(pendingIntent);*/

        ComponentName mediaButtonReceiverComponentName = new ComponentName(
                getApplicationContext(),
                SongBroadCast.class);
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mediaButtonReceiverComponentName);
        PendingIntent mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                0,
                mediaButtonIntent,
                0);
        mediaSession = new MediaSessionCompat(this,
                "RetroMusicPlayer",
                mediaButtonReceiverComponentName,
                mediaButtonReceiverPendingIntent);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );
        mediaSession.setCallback(mMediaSessionCallback);
        mediaSession.setActive(true);
        mediaSession.setMediaButtonReceiver(mediaButtonReceiverPendingIntent);

        setDefaultEqualizerValues();

        preferences = getSharedPreferences("history",MODE_PRIVATE);
        String list = preferences.getString("urls","");
        ArrayList<String> urls = new ArrayList<>();
        if (!Objects.requireNonNull(list).isEmpty()) {
            urls.addAll(Arrays.asList(list.split(",")));
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        actionPlay.setOnClickListener(v -> changePlayBack(!isplaying));
        bottom_player.setOnClickListener(v-> openPlayer());
        actionUp.setOnClickListener(v->openPlayer());

        fragmentManager = getSupportFragmentManager();
        localMusicFrag = new LocalMusicFragment();
        localSearchFrag = new LocalSearchFragment();
        HistoryFrag = new HistoryFragment();
        libraryFrag = new LibraryFragment();
        FavouriteFrag = new OPlaylistFragment();
        SearchFrag = new SearchFragment();
        PlaylistFrag = new PlaylistFragment();
        NCFrag = new NCFragment();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (YTutils.isInternetAvailable())
        {
            if (getYTUrls("blank").length>1)
                loadFragment(HistoryFrag);
            else navigation.setSelectedItemId(R.id.navigation_search);
        }
        else {
            loadFragment(NCFrag);
        }

        try {
            FFmpeg ffmpeg = FFmpeg.getInstance(this);
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {
                    supportFFmpeg=true;
                    Log.e(TAG, "onSuccess: FFMPEG Loaded");
                }

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            Log.e(TAG, "onCreate: FFMpeg not supported");
            e.printStackTrace();

        }
       /* File file = YTutils.getFile("Download/9WR9YF2.csv");
        Date lastModDate = new Date(file.lastModified());
        Log.e(TAG, "onCreate: Last modified "+lastModDate.toString());*/
    }

    void setDefaultEqualizerValues() {
        boolean isSet = settingPref.getBoolean("equalizer_default",false);
        if (!isSet) {
            SharedPreferences.Editor editor = settingPref.edit();
            int sessionId = player.getAudioComponent().getAudioSessionId();
            mEqualizer = new Equalizer(1000,sessionId);
            short numberFrequencyBands = mEqualizer.getNumberOfBands();
            final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
            final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

            editor.putInt("bandLength",numberFrequencyBands);
            editor.putInt("lowerBand",lowerEqualizerBandLevel);
            editor.putInt("higherBand",upperEqualizerBandLevel);
            editor.putBoolean("equalizer_default",true);
            editor.apply();

            mEqualizer.release();
        }
    }

    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    public static void updateMediaSessionPlaybackState() {
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(MEDIA_SESSION_ACTIONS)
                .setState(isplaying ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                        player.getCurrentPosition(), 1);
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    public static MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onPlay() {
            super.onPlay();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onSeekTo(long pos) {
            Log.e(TAG, "onSeekTo: Playing"+pos );
            player.seekTo(pos);
            super.onSeekTo(pos);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
        }
    };

    public static void PlayVideo_Local(String[] urls) {
        /** YTUrls here will work as path to music file...
         *
         *  Background task will load all the details about music
         *  and will set it to player and respective fields.
         */

        localPlayBack=true;
        yturls.clear();
        if (LoadOffline !=null && LoadOffline.getStatus() == AsyncTask.Status.RUNNING)
        {
            player.stop(); player.release();
            LoadOffline.cancel(true);
        }
        yturls.addAll(Arrays.asList(urls));
        ytIndex = 0;
        LoadOffline = new loadVideo_Local(urls[ytIndex]);
        LoadOffline.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void PlayVideo_Local(String[] urls,int position) {
        /** YTUrls here will work as path to music file...
         *
         *  Background task will load all the details about music
         *  and will set it to player and respective fields.
         */

        localPlayBack=true;
        yturls.clear();
        if (LoadOffline !=null && LoadOffline.getStatus() == AsyncTask.Status.RUNNING)
        {
            player.stop(); player.release();
            LoadOffline.cancel(true);
        }
        yturls.addAll(Arrays.asList(urls));
        ytIndex = position;
        LoadOffline = new loadVideo_Local(urls[ytIndex]);
        LoadOffline.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void ChangeVideoOffline(int position) {
        if (LoadOffline !=null && LoadOffline.getStatus() == AsyncTask.Status.RUNNING)
        {
            player.stop(); player.release();
            LoadOffline.cancel(true);
        }
        ytIndex = position;
        LoadOffline = new loadVideo_Local(yturls.get(ytIndex));
        LoadOffline.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void PlayVideo(String[] ytUrls) {
        localPlayBack=false;
        yturls.clear();
        if (LoadVideo !=null && LoadVideo.getStatus() == AsyncTask.Status.RUNNING)
        {
            player.stop(); player.release();
            LoadVideo.cancel(true);
        }
        yturls.addAll(Arrays.asList(ytUrls));
        videoID = YTutils.getVideoID(yturls.get(0));
        Log.e("VideoID_ToPLAY",videoID+"");
        LoadVideo = new loadVideo();
        LoadVideo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,videoID);
    }

    public static void PlayVideo(String[] ytUrls, int position) {
        localPlayBack=false;
        yturls.clear();
        if (LoadVideo !=null && LoadVideo.getStatus() == AsyncTask.Status.RUNNING)
        {
            player.stop(); player.release();
            LoadVideo.cancel(true);
        }
        yturls.addAll(Arrays.asList(ytUrls));
        ytIndex = position;
        videoID = YTutils.getVideoID(yturls.get(position));
        LoadVideo = new loadVideo();
        LoadVideo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,videoID);
    }

    public static void ChangeVideo(int position) {
        if (LoadVideo !=null && LoadVideo.getStatus() == AsyncTask.Status.RUNNING)
        {
            player.stop(); player.release();
            LoadVideo.cancel(true);
        }
        ytIndex = position;
        videoID = YTutils.getVideoID(yturls.get(position));
        LoadVideo = new loadVideo();
        LoadVideo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,videoID);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (getSupportFragmentManager().getBackStackEntryCount()>0) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        if (fragment instanceof SFragment) {
            loadFragment(SearchFrag);
            return;
        }
        if (fragment instanceof DiscoverFragment) {
            loadFragment(SearchFrag);
            return;
        }

        if (fragment instanceof LocalSearchFragment) {
            loadFragment(localMusicFrag);
            return;
        }

        if (fragment instanceof OPlaylistFragment && fragment.getTag()!=null && fragment.getTag().equals("localMusic")) {
            loadFragment(localMusicFrag);
            return;
        }

        if (fragment instanceof OPlaylistFragment && loadedFavFrag) {
            loadedFavFrag=false;
            loadFragment(libraryFrag);
            return;
        }

        if (fragment instanceof OPlaylistFragment) {
            loadFragment(PlaylistFrag);
            return;
        }

        if (fragment instanceof PlaylistFragment) {
            loadFragment(libraryFrag);
            return;
        }

        if (fragment instanceof LocalMusicFragment) {
            loadFragment(libraryFrag);
            return;
        }


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back once more to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_history:
                    if (YTutils.isInternetAvailable()) {
                        loadFragment(HistoryFrag);
                    }else loadFragment(NCFrag);
                    return true;
                case R.id.navigation_search:
                    loadFragment(SearchFrag);
                    return true;
                case R.id.navigation_playlist:
                    loadFragment(libraryFrag);
   //                 loadFragment(PlaylistFrag);
                    return true;
            }

            return true;
        }
    };

    public static boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            Log.e("LoadingFragment","");

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        CheckIntent(intent);
    }

    public static void loadPlayFrag() {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PlaylistFrag)
                .commit();
    }

    public static void loadLibraryFrag() {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, libraryFrag)
                .commit();
    }

    public static void loadPreviousFrag() {
        if (fragmentManager.getBackStackEntryCount()>0) {
            fragmentManager.popBackStack();
        }else
            activity.onBackPressed();
    }

    public static void loadLocalMusicFrag() {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, localMusicFrag)
                .commit();
    }

    public static void loadSearchFrag() {
       fragmentManager.beginTransaction()
               .replace(R.id.fragment_container, SearchFrag)
               .commit();
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.deleteNotificationChannel("channel_01");
        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.cancel(1);
       try {
           if (PlayerActivity.datasync.getStatus() == AsyncTask.Status.RUNNING)
               PlayerActivity.datasync.cancel(true);
           player.stop();
           player.release();

           PlayerActivity.mHandler.removeCallbacks(PlayerActivity.mUpdateTimeTask);
       }catch (Exception e) { e.printStackTrace(); }
        super.onDestroy();
    }

    @Override
    public void onRemoveFromHistory(int position) {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                instanceof HistoryFragment)
        HistoryFragment.removeFromHistory(position);
        if (yturls.size()>0) {
            try {
                yturls.remove(position);
            }catch (Exception e) {e.printStackTrace();}
        }if (nPlayModels.size()>0) {
           try {
               nPlayModels.remove(position);
           }catch (Exception e){ e.printStackTrace(); }
        }
    }

    @Override
    public void ReplaceFragment() {
        if (YTutils.isInternetAvailable()) {
            HistoryFrag = new HistoryFragment();
            loadFragment(HistoryFrag);
        } else Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(TextView view) {
        selectedItemText = view.getText().toString();
        switch (selectedItemText) {
            case "5 minutes":
                sleepSeconds = 5*60;
                break;
            case "10 minutes":
                sleepSeconds = 10*60;
                break;
            case "15 minutes":
                sleepSeconds = 15*60;
                break;
            case "30 minutes":
                sleepSeconds = 30*60;
                break;
            case "45 minutes":
                sleepSeconds = 45*60;
                break;
            case "60 minutes":
                sleepSeconds = 60*60;
                break;
            case "End of track":
                sleepSeconds = -2;
                break;
            case "None":
                selectedItemText="";
                sleepSeconds=0;
                break;
        }
        libraryFrag.onActivityResult(100,0,null);
    }

    void openPlayer() {
        Intent i=new Intent(MainActivity.this,PlayerActivity2.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
       /* i.putExtra("youtubelink",new String[]{ytLink});
        i.putExtra("isNewIntent","true");
        if (enablePlayback)
        i.putExtra("changePlayback",changePlayBack);*/
        startActivity(i);
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }


    @SuppressLint("StaticFieldLeak")
    boolean CheckIntent(Intent incoming) {
        if (Intent.ACTION_SEND.equals(incoming.getAction())
                && incoming.getType() != null && "text/plain".equals(incoming.getType())) {
            Log.e("Firing","checkIntent");
            String ytLink = incoming.getStringExtra(Intent.EXTRA_TEXT);
            Log.e("IntentYTLink",ytLink+"");
            if (YTutils.isValidID(ytLink)) {
                if (yturls.size()<=0) {
                    PlayVideo(getYTUrls(ytLink),0);
                }else {
                    int insert_pos = ytIndex;
                    if (localPlayBack) {
                        Log.e(TAG, "CheckIntent: Running this one" );
                        localPlayBack=false;
                        PlayVideo(getYTUrls(ytLink),0);
                        return true;
                    }
                    if (nPlayModels.size()>0 && nPlayModels.size()==yturls.size()) {
                        new AsyncTask<Void,Void,Void>(){
                            YTMeta ytMeta;
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                if (ytMeta.getVideMeta()!=null) {
                                    NPlayModel model = new NPlayModel(ytLink,ytMeta,false);
                                    for (NPlayModel model1 : nPlayModels) {
                                        if (model1.getUrl().equals(model.getUrl()))
                                        {
                                            nPlayModels.remove(model1);
                                            break;
                                        }
                                    }
                                    nPlayModels.add(insert_pos,model);
                                }else
                                    Toast.makeText(activity, "Unexpected parsing error occurred!", Toast.LENGTH_SHORT).show();
                                super.onPostExecute(aVoid);
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                ytMeta = new YTMeta(YTutils.getVideoID(ytLink));
                                return null;
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    if (yturls.contains(ytLink))
                        yturls.remove(ytLink);
                    yturls.add(insert_pos,ytLink);
                    ChangeVideo(insert_pos);
                }
                return true;
            }else if (ytLink.contains("open.spotify.com")&&ytLink.contains("/track/")) {
                new makeSpotifyData(ytLink).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
            }else {
                YTutils.showAlert(MainActivity.this,"Callback Error",
                        "The requested url is not a valid YouTube url", true);
                return true;
            }
        }
        return false;
        /*ytLink = incoming.getStringExtra("videoID");
        Log.e("YouTubeUrl",ytLink+"");
        String playerCheck = incoming.getStringExtra("is_playing");
        if (playerCheck!=null) {
            bottom_player.setVisibility(View.VISIBLE);
            actionTitle.setSelected(true);
            actionTitle.setText(incoming.getStringExtra("b_title"));
            bottom_player.setOnClickListener(v -> openPlayer(false,null));
            actionUp.setOnClickListener(v -> openPlayer(false,null));
            if (playerCheck.equals("true")) {
                actionPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle));
                actionPlay.setOnClickListener(v -> openPlayer(true,"true"));
            }else {
                actionPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle));
                actionPlay.setOnClickListener(v -> openPlayer(true,"false"));
            }

        }*/

        /*if (incoming.getData()!=null) {
            String ytLink = incoming.getData().toString();
            Log.e("IntentYTLink",ytLink+"");
            if (YTutils.isValidID(ytLink)){
                Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("youtubelink",new String[] {ytLink});
                startActivityForResult(intent,200);
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            }else if (ytLink.contains("open.spotify.com")&&ytLink.contains("/track/")) {
                new MainActivity.getData(ytLink).execute();
            }else {
                YTutils.showAlert(MainActivity.this,"Callback Error",
                        "The requested url is not a valid YouTube url", true);
            }
        }*/
    }

    /*class getData extends AsyncTask<Void,Void,Void> {

        String spotifyUrl,ytLink;
        ProgressDialog dialog;
        public getData(String videoID) {
            this.spotifyUrl = videoID;
            dialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            if (ytLink!=null) {
                Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
                intent.putExtra("youtubelink",new String[] {ytLink});
                startActivityForResult(intent,200);
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setMessage("Parsing spotify url...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("Original_URL",spotifyUrl+"");
            SpotifyTrack track = new SpotifyTrack(YTutils.getSpotifyID(spotifyUrl));
            ytLink = track.getYtUrl();
            Log.e("GOTURL_Here",ytLink+"");
            return null;
        }
    }*/

    /**
    * Implementing a new player within main activity itself...
    */

    public static String videoTitle = "", channelTitle = "", viewCounts, imgUrl, videoID,audioLink;
    public static int likeCounts,dislikeCounts;
    public static NotificationManagerCompat notificationManagerCompat;
    static NotificationManager notificationManager;
    static NotificationChannel notificationChannel;
    static PendingIntent prevPendingIntent,pausePendingIntent,nextPendingIntent,clickPendingIntent,favouritePendingIntent;
    public static Bitmap bitmapIcon; static ArrayList<YTConfig> ytConfigs;
    static NotificationCompat.Builder builder;
    public static boolean isplaying, sleepEndTrack=false,localPlayBack=false,isFavourite=false,isEqualizerEnabled=false;
    public static boolean isLoop=false,isEqualizerSet=false;
    static Handler mHandler = new Handler();
    static long total_duration = 0;
    public static int total_seconds; public static int nColor;
    public static ArrayList<String> yturls;
    public static int ytIndex = 0;
    public static Equalizer mEqualizer;

    static class loadVideo_Local extends AsyncTask<Void,Void,Void> {
        String filePath;

        public loadVideo_Local(String filePath) {
            this.filePath = filePath;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
          try {
              Palette.generateAsync(bitmapIcon, new Palette.PaletteAsyncListener() {
                  public void onGenerated(Palette palette) {
                      nColor = palette.getVibrantColor(activity.getResources().getColor(R.color.light_white));
                      continueinMainThread("isPath:"+filePath);
                  }
              });
          }catch (Exception e){
              Log.e(TAG, "onPostExecute: "+e.getMessage());
          }
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File f = new File(filePath);
            Uri uri = Uri.fromFile(f);
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(activity,uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                byte [] data = mmr.getEmbeddedPicture();

                if(data != null)
                    bitmapIcon = BitmapFactory.decodeByteArray(data, 0, data.length);
                else
                    bitmapIcon = YTutils.drawableToBitmap(ContextCompat.getDrawable(activity,R.drawable.ic_pulse));

                if (artist==null) artist ="Unknown artist";
                if (title==null) title = YTutils.getVideoTitle(f.getName());

                if (title.contains("."))
                    title = title.split("\\.")[0];

                videoTitle = title;
                channelTitle = artist;
                likeCounts = -1; dislikeCounts = -1;
                viewCounts = "-1";

                videoID = f.getPath();

                total_seconds = Integer.parseInt(durationStr);

            }catch (Exception e) {
                // TODO: Do something when cannot played...
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            commonPreExecute();
            super.onPreExecute();
        }
    }

    static class loadVideo extends AsyncTask<String,String,Void> {

        @SuppressLint("StaticFieldLeak")
        @Override
        protected void onPostExecute(Void aVoid) {

            Glide.with(activity)
                    .asBitmap()
                    .load(imgUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Palette.generateAsync(resource, new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    bitmapIcon = resource;
                                    nColor = palette.getVibrantColor(activity.getResources().getColor(R.color.light_white));
                                }
                            });

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

            new YouTubeExtractor(activity) {

                @Override
                protected void onPostExecute(SparseArray<YtFile> ytFiles) {

                    if (ytFiles == null) {
                        parseVideoNewMethod(YTutils.getYtUrl(videoID),videoTitle);
                        return;
                    }

                    YtFile ytaudioFile = getBestStream(ytFiles);
                    if (ytaudioFile.getUrl() == null) {
                        parseVideoNewMethod(YTutils.getYtUrl(videoID), videoTitle);
                        return;
                    } else {
                        audioLink = ytaudioFile.getUrl();
                        audioLink = audioLink.replace("\\", "");

                        Log.e("PlayerActivity", "videoTitle: " + videoTitle + ", channelTitle: " + channelTitle);

                        Log.e("PlayerActivity", "Stream: " + audioLink);
                        ytConfigs.clear();
                        for (int i = 0, itag; i < ytFiles.size(); i++) {
                            itag = ytFiles.keyAt(i);
                            YtFile ytFile = ytFiles.get(itag);

                            if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                                addFormatToList(videoTitle, ytFile,channelTitle);
                            }
                        }

                        continueinMainThread(audioLink);
                    }

                    super.onPostExecute(ytFiles);
                }

                @Override
                protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                    if (videoTitle.isEmpty() && videoMeta.getTitle() != null) {
                        channelTitle = videoMeta.getAuthor();
                        videoTitle = YTutils.setVideoTitle(videoMeta.getTitle());
                    }
                }
            }.execute(YTutils.getYtUrl(videoID));
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... arg0) {
            videoID = arg0[0];

            int i=0;
            int apiLength = API_KEYS.length;
            String json;
            do {
                json = jsonResponse(videoID, i);
                i++;
            }while (json.contains("\"error\":") && i<apiLength);

            YTMeta ytMeta = new YTMeta(videoID);
            if (ytMeta.getVideMeta() != null) {
                MainActivity.channelTitle = ytMeta.getVideMeta().getAuthor();
                MainActivity.videoTitle = YTutils.setVideoTitle(ytMeta.getVideMeta().getTitle());
                MainActivity.imgUrl = ytMeta.getVideMeta().getImgUrl();
            }

            if (json.contains("\"error\":")) {
                YTStatistics ytStatistics = new YTStatistics(videoID);
                MainActivity.viewCounts = ytStatistics.getViewCount();
                MainActivity.likeCounts = Integer.parseInt(ytStatistics.getLikeCount());
                MainActivity.dislikeCounts = Integer.parseInt(ytStatistics.getDislikeCount());
                json = null;
            }

            if (json != null) {
                try {
                    JSONObject statistics = new JSONObject(json).getJSONArray("items")
                            .getJSONObject(0).getJSONObject("statistics");
                    viewCounts = YTutils.getViewCount(Long.parseLong(statistics.getString("viewCount")));
                    likeCounts = 100;
                    dislikeCounts = 0;
                    try {
                        likeCounts = Integer.parseInt(statistics.getString("likeCount"));
                        dislikeCounts = Integer.parseInt(statistics.getString("dislikeCount"));
                    }catch (Exception e){e.printStackTrace();}

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("PlayerActivity_JSON", e.getMessage());
                }
            }
            return null;
        }

        String jsonResponse(String videoID, int apinumber) {
            HttpHandler httpHandler = new HttpHandler();
            String link = "https://www.googleapis.com/youtube/v3/videos?id=" + videoID + "&key=" + API_KEYS[apinumber] + "&part=statistics";
            return httpHandler.makeServiceCall(link);
        }

        @Override
        protected void onPreExecute() {
            commonPreExecute();
            super.onPreExecute();
        }
    }

    static void commonPreExecute() {
        adViewLayout.setVisibility(VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                adView.setVisibility(VISIBLE);
                super.onAdLoaded();
            }
        });
        bottom_player.setVisibility(VISIBLE);
        onClear();
    }

    private static void parseVideoNewMethod(String yturl, String videoTitle) {
        new YoutubeStreamExtractor(new YoutubeStreamExtractor.ExtractorListner(){

            @Override
            public void onExtractionDone(List<YoutubeMedia> adativeStream, List<YoutubeMedia> muxedStream, YoutubeMeta meta) {
                if (muxedStream.isEmpty()) {
                    playNext();
                    Toast.makeText(activity, videoTitle+": Couldn't get the required audio stream!", Toast.LENGTH_SHORT).show();
                   // showAlert("Failed!", "Couldn't get the required audio stream. Try again!", true);
                    return;
                }

                Log.e("Method2","Extracted using new method");

                ytConfigs.clear();

                List<YoutubeMedia> bestStream = getBestStream(adativeStream);

                for(int i=0; i<bestStream.size();i++) addVideoToList(bestStream.get(i),videoTitle,channelTitle);

                continueinMainThread(audioLink);
            }

            @Override
            public void onExtractionGoesWrong(final ExtractorException e) {
               // showAlert("Failed!", "Couldn't get the required audio stream. Try again!", true);
                playNext();
                Toast.makeText(activity, videoTitle+": Couldn't get the required audio stream!", Toast.LENGTH_SHORT).show();
            }
        }).Extract(YTutils.getVideoID(yturl));
    }

    public static void actionFavouriteClicked() {
        if (MainActivity.total_seconds==0)
        {
            Toast.makeText(activity, "Player is still processing!", Toast.LENGTH_SHORT).show();
        }else{
            try {
                if (!MainActivity.isFavourite)
                    PlayerActivity2.favouriteButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite_full));
                else PlayerActivity2.favouriteButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_favorite));
            }catch (Exception ignored){}

            MainActivity.write_Favourite();
            rebuildNotification();
        }
    }

    public static void write_Favourite() {
        String t = YTutils.readContent(activity,"favourite.csv");
        if (t!=null && !t.contains(MainActivity.videoID)) {
            t += "\n"+MainActivity.videoID+"|"+MainActivity.total_seconds;
            Toast.makeText(activity, "Added to favourites!", Toast.LENGTH_SHORT).show();
            MainActivity.isFavourite=true;
        }else if (t!=null && t.contains(MainActivity.videoID)) {

            String[] lines = t.split("\n|\r");
            StringBuilder builder = new StringBuilder();
            for (String line : lines) {
                if (!line.contains(MainActivity.videoID) && !line.isEmpty()) {
                    builder.append("\n").append(line);
                }
            }

            t = builder.toString().trim();

            Toast.makeText(activity, "Removed from favourites!", Toast.LENGTH_SHORT).show();
            MainActivity.isFavourite=false;
        }else {
            t = MainActivity.videoID+"|"+MainActivity.total_seconds;
            Toast.makeText(activity, "Added to favourites!", Toast.LENGTH_SHORT).show();
            MainActivity.isFavourite=true;
        }
        YTutils.writeContent(activity,"favourite.csv",t.trim());
    }

    static boolean dontAllowToPlay=false;
    private static void continueinMainThread(String link) {

        player.stop();
        player.release();
        if (link.startsWith("isPath:"))
        {
            link = link.split(":")[1];
            File f = new File(link);
            mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.fromFile(f));
        }else
        mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(link));
        player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        makePause();
        isplaying = true;

        player.addListener(new Player.EventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_BUFFERING:
                        break;
                    case ExoPlayer.STATE_ENDED:
                        makePlay();
                        isplaying = false;
                        playNext();
                        Log.e(TAG, "onPlayerStateChanged: State Changed "+MainActivity.sleepEndTrack );
                        if (sleepEndTrack) {
                            Log.e(TAG, "onPlayerStateChanged: tiggered" );
                            sleepEndTrack=false;
                            dontAllowToPlay=true;
                        }
                        break;
                    case ExoPlayer.STATE_READY:
                        actionTitle.setVisibility(VISIBLE);
                        actionTitle.setText(videoTitle);
                        loadProgress.setVisibility(View.GONE);
                        songProgress.setVisibility(VISIBLE);
                        actionPlay.setVisibility(VISIBLE);
                        actionUp.setVisibility(VISIBLE);
                        total_duration = MainActivity.player.getDuration();
                        total_seconds = (int) total_duration / 1000;
                        try {
                            PlayerActivity2.loadAgain();
                            PlayerActivity2.totalDuration.setText(YTutils.milliSecondsToTimer(MainActivity.total_duration));
                        }catch (Exception e) { Log.e("PlayerActivity","not loaded yet!"); }

                        addEqualizer();

                        /** Setting mediaSession metadata */
                        final MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, channelTitle)
                                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, channelTitle)
                                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, videoTitle)
                                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, player.getDuration())
                                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, null);
                        mediaSession.setMetadata(metaData.build());

                        rebuildNotification();
                        updateProgressBar();
                        if (dontAllowToPlay)
                        {
                            dontAllowToPlay=false;
                            libraryFrag.onActivityResult(101,0,null);
                            changePlayBack(false);
                        }
                        break;
                }
            }
        });

        // Store video into history
        if (!localPlayBack)
        new saveToHistory().execute(YTutils.getYtUrl(videoID));
    }

    public static void addEqualizer() {
        int audioSessionId = player.getAudioComponent().getAudioSessionId();
        Log.e(TAG, "onAudioSessionId: AudioSessionID: "+audioSessionId );
        mEqualizer = new Equalizer(1000, audioSessionId);
        mEqualizer.setEnabled(isEqualizerEnabled);
        isEqualizerSet=true;
        int current = settingPref.getInt("position", 0);
        if (current == 0) {
            for (short seek_id = 0; seek_id < mEqualizer.getNumberOfBands(); seek_id++) {
                int progressBar = settingPref.getInt("seek_" + seek_id, 1500);
                short equalizerBandIndex = seek_id;
                final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
                Log.i("seek_" + seek_id, ":" + progressBar);
                if (progressBar != 1500) {
                    mEqualizer.setBandLevel(equalizerBandIndex,
                            (short) (progressBar + lowerEqualizerBandLevel));
                } else {

                    mEqualizer.setBandLevel(equalizerBandIndex,
                            (short) (progressBar + lowerEqualizerBandLevel));
                }
            }
        } else {
            mEqualizer.usePreset((short) (current - 1));
        }
    }

    private static void addFormatToList(final String videoTitle, final YtFile ytfile, final String channelTitle) {
        Format ytFrVideo = ytfile.getFormat();

        String ytText; boolean isaudio=false;
        if (ytFrVideo.getHeight() == -1)
        {
            isaudio = true;
            ytText = "Audio " + ytFrVideo.getAudioBitrate() + " kbit/s";
        }
        else {
            ytText = (ytFrVideo.getFps() == 60) ? "Video " + ytFrVideo.getHeight() + "p60" :
                    "Video " + ytFrVideo.getHeight() + "p";
            if (ytfile.getFormat().getAudioBitrate() == -1) {
                ytText += " (no audio)";
            }
        }
        if (ytText.contains("128 kbit/s"))
            audioLink = ytfile.getUrl();

        ytConfigs.add(new YTConfig(ytText, ytfile.getUrl(), ytfile.getFormat().getExt(), videoTitle, channelTitle,isaudio));
    }

    private static void addVideoToList(final YoutubeMedia media, final String videoTitle, final String channelTitle) {

        String ytText;boolean isaudio=false;
        if (media.getResSize()!=null) {
            ytText = "Video "+media.getResolution();
            if (media.isVideoOnly()) {
                ytText+=" (no audio)";
            }
            Log.e("VideoUrlFound",media.getUrl()+"");
        }else {
            isaudio = true;
            ytText = "Audio "+YTutils.getAvgBitRate(Integer.parseInt(media.getBitrate()))+" kbit/s";
            if (media.getCodec().contains("mp4a")){
                audioLink = media.getUrl();
                media.setExtension("m4a");
                Log.e("AudioSpecialLink",media.getUrl()+"");
            }else {
                Log.e("AudioURLFOUND",media.getUrl()+"");
            }
        }
        ytConfigs.add(new YTConfig(ytText, media.getUrl(), media.getExtension(), videoTitle,channelTitle,isaudio));
    }


    public static void rebuildNotification() {
        boolean setgoing = true;
        int icon = R.drawable.ic_pause_notify;
        int favicon = R.drawable.ic_favorite;
        if (!isplaying) {
            icon = R.drawable.ic_play_notify;
            setgoing = false;
        }
        if (isFavourite)
            favicon = R.drawable.ic_favorite_full;

        builder = new NotificationCompat.Builder(activity, "channel_01")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.ic_music)
                .addAction(R.drawable.ic_previous_notify, "Previous", prevPendingIntent)
                .addAction(icon, "Pause", pausePendingIntent)
                .addAction(R.drawable.ic_next_notify, "Next", nextPendingIntent)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                .setMediaSession(mediaSession.getSessionToken()))
                .setContentTitle(videoTitle)
                .setOngoing(setgoing)
                .setSound(null)
                .setContentIntent(clickPendingIntent)
                .setContentText(channelTitle)
                .setLargeIcon(bitmapIcon);

        if (!localPlayBack)
            builder.addAction(favicon,"Favourite",favouritePendingIntent);

        notificationManagerCompat.notify(1, builder.build());
    }



    public void createNotification() {

        /** Next song Listener */
        Intent newintent = new Intent(this, SongBroadCast.class);
        newintent.setAction("com.kpstv.youtube.ACTION_NEXT");
        nextPendingIntent = PendingIntent.getBroadcast(this, 1, newintent, 0);
        /** Previous song Listener */
        newintent = new Intent(this, SongBroadCast.class);
        newintent.setAction("com.kpstv.youtube.ACTION_PREVIOUS");
        prevPendingIntent = PendingIntent.getBroadcast(this, 2, newintent, 0);
        /** Play or Pause listener */
        newintent = new Intent(this, SongBroadCast.class);
        newintent.setAction("com.kpstv.youtube.ACTION_PLAY");
        pausePendingIntent = PendingIntent.getBroadcast(this, 3, newintent, 0);
        /** Focus on Click Listener */
        newintent = new Intent(MainActivity.this, MainActivity.class);
        newintent.putExtra("DO", "focus");
        clickPendingIntent = PendingIntent.getActivity(this, 4, newintent, 0);
        /** Favourite Pending intent */
        newintent = new Intent(this,SongBroadCast.class);
        newintent.setAction("com.kpstv.youtube.FAVOURITE_SONG");
        favouritePendingIntent = PendingIntent.getBroadcast(this,10,newintent,0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            notificationChannel = new NotificationChannel("channel_01", name, importance);
            notificationChannel.setDescription(description);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManagerCompat = NotificationManagerCompat.from(this);
    }

    public static void changePlayBack(boolean isplay) {
        Log.e("PlayingState", "Playing State: " + player.isPlayingAd() + ", isPlay:" + isplay);
        if (isplay) {
            makePause();
            MainActivity.player.setPlayWhenReady(true);
            //   updateDuration();
        } else {
            makePlay();
            MainActivity.player.setPlayWhenReady(false);
            // mTimer.cancel();
        }
        Log.e("CurrentDur", MainActivity.player.getCurrentPosition() + "");
        isplaying = isplay;
        rebuildNotification();
    }

    public static void onClear() {
        loadProgress.setVisibility(VISIBLE);
        actionTitle.setVisibility(View.GONE);
        songProgress.setVisibility(View.GONE);
        actionPlay.setVisibility(View.GONE);
        songProgress.setProgress(0);
        actionUp.setVisibility(View.GONE);
        player.stop();
        player.release();
        mHandler.removeCallbacks(mUpdateTimeTask);
        isplaying = false;
        total_duration = 0;
        total_seconds = 0;
    }

    public static void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    public static Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = MainActivity.player.getDuration();
            long currentDur = MainActivity.player.getCurrentPosition();
            int progress = (YTutils.getProgressPercentage(currentDur, totalDuration));
            songProgress.setProgress(progress);

            updateMediaSessionPlaybackState();
            //mMediaSessionCallback.onSeekTo(currentDur);

            mHandler.postDelayed(this, 100);
        }
    };

    static String[] getYTUrls(String to_inject_yturl) {
        preferences = activity.getSharedPreferences("history",MODE_PRIVATE);
        String line = preferences.getString("urls","");
        if (line!=null && !line.isEmpty()) {
            String[] lines = line.split(",");

            ArrayList<String> arrayList = new ArrayList<>();

        //    String[] yt_urls = new String[1+lines.length];
            Log.e(TAG, "getYTUrls: Injected uri 0: "+to_inject_yturl );
            arrayList.add(to_inject_yturl);
         //   yt_urls[0] = to_inject_yturl;
            int i=1;
            for(String l: lines) {
                l = l.split("\\|")[0];
                if (l.isEmpty()) continue;
                if (YTutils.getVideoID(l).equals(YTutils.getVideoID(to_inject_yturl))) continue;
                arrayList.add(l);
            //    yt_urls[i] = l;
                Log.e(TAG, "getYTUrls: Injected uri "+i+": "+l);
//                i++;
            }
            nPlayModels.clear();

            /*for (int i=1;i<yt_urls.length;i++)
                yt_urls[i] = lines[i-1].split("\\|")[0];*/
            return YTutils.convertListToArrayMethod(arrayList);
        }else {
            String[] yt_urls = new String[1];
            yt_urls[0] = to_inject_yturl;
            return yt_urls;
        }
    }

    static class makeSpotifyData extends AsyncTask<Void,Void,Void> {
        SpotifyTrack track;
        String spotifyUrl,ytLink;
        public makeSpotifyData(String yturl) {
            this.spotifyUrl = yturl;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (ytLink!=null) {
                if (yturls.size()<=0) {
                    PlayVideo(getYTUrls(ytLink),0);
                } else {
                    int insert_index = ytIndex;
                    if (nPlayModels.size()>0 && nPlayModels.size()==yturls.size()) {
                        MetaModel metaModel = new MetaModel(track.getTitle(),track.getAuthor(),track.getImageUrl());
                        NPlayModel model = new NPlayModel(ytLink,new YTMeta(metaModel),true);
                        nPlayModels.add(insert_index,model);
                    }
                    if (yturls.contains(ytLink))
                        yturls.remove(ytLink);
                    yturls.add(insert_index,ytLink);
                    ChangeVideo(insert_index);
                }
            }else {
                Toast.makeText(activity, "Couldn't parse this Spotify url", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            onClear();
            bottom_player.setVisibility(VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            track = new SpotifyTrack(YTutils.getSpotifyID(spotifyUrl));
            ytLink = track.getYtUrl();
            return null;
        }
    }



    public static void playPrevious() {
        if (ytIndex <= 0) {
            Toast.makeText(activity, "No previous song in playlist", Toast.LENGTH_SHORT).show();
            return;
        }
        onClear();
        videoID = YTutils.getVideoID(yturls.get(ytIndex-1));
        ytIndex--;
        if (!localPlayBack) {
            LoadVideo = new loadVideo();
            LoadVideo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,videoID);
        } else {
            videoID = yturls.get(ytIndex);
            LoadOffline = new loadVideo_Local(yturls.get(ytIndex));
            LoadOffline.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public static void playNext() {
        if ((ytIndex + 1) == yturls.size()) {
            if (isLoop) {
                ytIndex=-1;
            }else {
                Toast.makeText(activity, "No new song in playlist", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        onClear();
        videoID = YTutils.getVideoID(yturls.get(ytIndex+1));
        ytIndex++;
        if (!localPlayBack) {
            LoadVideo = new loadVideo();
            LoadVideo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,videoID);
        }else {
            videoID = yturls.get(ytIndex);
            LoadOffline = new loadVideo_Local(yturls.get(ytIndex));
            LoadOffline.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    static void makePlay() {
        actionPlay.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_play_circle));
        try {
            PlayerActivity2.makePlay();
        }catch (Exception ignored) {
            Log.e("PlayerActivity","isnull");
        }
    }

    static void makePause() {
        actionPlay.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pause_circle));
        try {
            PlayerActivity2.makePause();
        }catch (Exception ignored) {
            Log.e("PlayerActivity","isnull");
        }
       /* playFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        collpaseView.setImageViewResource(R.id.nPlay, R.drawable.ic_pause_notify);
        expandedView.setImageViewResource(R.id.nPlay, R.drawable.ic_pause_notify);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            notificationManager.notify(1,builder.build());
        }
        notificationManagerCompat.notify(1, builder.build());*/
    }

    private static List<YoutubeMedia> getBestStream(List<YoutubeMedia> streams) {
        List<YoutubeMedia> medias = new ArrayList<>();
        for(int i=0; i<streams.size();i++) {
            YoutubeMedia media = streams.get(i);
            if (!media.isAudioOnly()) {
                int j=0;
                while (j<streams.size()) {
                    YoutubeMedia media1 = streams.get(j);
                    if (media.getResolution().equals(media1.getResolution())) {
                        int m1 = Integer.parseInt(media.getBitrate());
                        int m2 = Integer.parseInt(media1.getBitrate());
                        if (m2>m1) {
                            media=media1;
                        }
                    }
                    j++;
                }
                if (!medias.contains(media)) medias.add(media);
            }else {
                medias.add(media);
            }
        }
        return medias;
    }

    private static class saveToHistory extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... strings) {
            String url_link = strings[0];
            String set = preferences.getString("urls", "");

            // Get playlist
            ArrayList<String> urls = new ArrayList<>();
            if (!Objects.requireNonNull(set).isEmpty()) {
                urls.addAll(Arrays.asList(set.split(",")));
            }

            // Add to playlist by removing it first
            for (int i = 0; i < urls.size(); i++) {
                if (urls.get(i).contains(Objects.requireNonNull(YTutils.getVideoID(url_link)))) {
                    urls.remove(i);
                }
            }
            String formattedDate = YTutils.getTodayDate();
            int percent = 100;
            try {
                percent = likeCounts*100/(likeCounts+dislikeCounts);
            }catch (Exception e){e.printStackTrace();}
            Log.e("StringtoAdd", url_link + "|" + formattedDate+"|"+percent);
            urls.add(0, url_link + "|" + formattedDate+"|"+percent);

            // Save playlist
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < urls.size(); i++) {
                sb.append(urls.get(i)).append(",");
            }
            SharedPreferences.Editor prefsEditor = preferences.edit();
            prefsEditor.putString("urls", sb.toString());
            prefsEditor.apply();
            return null;
        }
    }

    private static YtFile getBestStream(SparseArray<YtFile> ytFiles) {
        ConnectionQuality connectionQuality = ConnectionQuality.MODERATE;
        connectionQuality = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
        int[] itags = new int[]{251, 141, 140, 17};

        if (connectionQuality != null && connectionQuality != ConnectionQuality.UNKNOWN) {
            switch (connectionQuality) {
                case POOR:
                    itags = new int[]{17, 140, 251, 141};
                    break;
                case MODERATE:
                    itags = new int[]{251, 141, 140, 17};
                    break;
                case GOOD:
                    itags = new int[]{141, 251, 140, 17};
                    break;
                case EXCELLENT:
                    itags = new int[]{141, 251, 140, 17};
                    break;
            }
        }

        if (ytFiles.get(itags[0]) != null) {
            return ytFiles.get(itags[0]);
        } else if (ytFiles.get(itags[1]) != null) {
            return ytFiles.get(itags[1]);
        } else if (ytFiles.get(itags[2]) != null) {
            return ytFiles.get(itags[2]);
        }
        return ytFiles.get(itags[3]);
    }

    static void showAlert(String title, String message, boolean isalert) {
        int icon = android.R.drawable.ic_dialog_info;
        if (isalert) icon = android.R.drawable.ic_dialog_alert;
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    bottom_player.setVisibility(View.GONE);
                })
                .setIcon(icon)
                .show();

    }



   /* private void setListener() {
        // Play or Pause listener
        Intent newintent = new Intent(MainActivity.this, MainActivity.class);
        newintent.putExtra("DO", "play");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newintent, 0);

        expandedView.setOnClickPendingIntent(R.id.nPlay, pendingIntent);
        collpaseView.setOnClickPendingIntent(R.id.nPlay, pendingIntent);

        // Next song Listener
        newintent = new Intent(MainActivity.this, MainActivity.class);
        newintent.putExtra("DO", "next");
        pendingIntent = PendingIntent.getActivity(this, 1, newintent, 0);

        expandedView.setOnClickPendingIntent(R.id.nForward, pendingIntent);
        collpaseView.setOnClickPendingIntent(R.id.nForward, pendingIntent);

        // Previous song Listener
        newintent = new Intent(MainActivity.this, MainActivity.class);
        newintent.putExtra("DO", "previous");
        pendingIntent = PendingIntent.getActivity(this, 2, newintent, 0);

        expandedView.setOnClickPendingIntent(R.id.nPrevious, pendingIntent);
        collpaseView.setOnClickPendingIntent(R.id.nPrevious, pendingIntent);

        // Add to playlist Listener
        newintent = new Intent(MainActivity.this, MainActivity.class);
        newintent.putExtra("DO", "add");
        pendingIntent = PendingIntent.getActivity(this, 3, newintent, 0);

        expandedView.setOnClickPendingIntent(R.id.nAdd, pendingIntent);

        // Focus on Click Listener
        newintent = new Intent(PlayerActivity.this, PlayerActivity.class);
        newintent.putExtra("DO", "focus");
        pendingIntent = PendingIntent.getActivity(this, 4, newintent, 0);

        expandedView.setOnClickPendingIntent(R.id.mainlayout, pendingIntent);
        collpaseView.setOnClickPendingIntent(R.id.mainlayout, pendingIntent);
    }*/
}


