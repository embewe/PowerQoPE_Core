package za.ac.uct.cs.powerqope.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import za.ac.uct.cs.powerqope.AnalyticsEvents;
import za.ac.uct.cs.powerqope.AnalyticsTextViewHelper;
import za.ac.uct.cs.powerqope.Config;
import za.ac.uct.cs.powerqope.PlayerActivity;
import za.ac.uct.cs.powerqope.R;
import za.ac.uct.cs.powerqope.dns.ConfigurationAccess;
import za.ac.uct.cs.powerqope.util.PhoneUtils;
import za.ac.uct.cs.powerqope.util.WebSocketConnector;
import za.ac.uct.cs.powerqope.utils.GetSpeedTestHostsHandler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;
import static za.ac.uct.cs.powerqope.fragment.SpeedCheckerFragment.DATABASE_NAME;

import org.json.JSONException;
import org.json.JSONObject;


public class VideoTestFragment extends Fragment {
    LinearLayout results;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PlaybackStateListener playbackStateListener;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;

    String TAG = PlayerActivity.class.getName();
LinearLayout buttonView;
    private TextView titleTextView, titleTextView1;
    private TextView debugTextView;
    AnalyticsTextViewHelper analyticsViewHelper;
    static int position = 0;
    static int lastPosition = 0;
    GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    HashSet<String> tempBlackList;
    private SQLiteDatabase mDatabase;
    public VideoTestFragment() {
        // Required empty public constructor
    }
    Button button;
    private static ConfigurationAccess CONFIG = ConfigurationAccess.getLocal();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video_test, container, false);
        playbackStateListener = new PlaybackStateListener();
        playerView = v.findViewById(R.id.video_view);
        results = v.findViewById(R.id.results);
        buttonView = v.findViewById(R.id.button_start);
        titleTextView = v.findViewById(R.id.title_text_view);
        titleTextView1 = v.findViewById(R.id.title_text_view1);
        debugTextView = v.findViewById(R.id.debug_text_view);
        debugTextView = v.findViewById(R.id.debug_text_view);
        button = v.findViewById(R.id.buttonStart);

        titleTextView.setText("Add Time Here");
        final DecimalFormat dec = new DecimalFormat("#.##");
        mDatabase = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS video (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT video_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    buffer varchar(200) NOT NULL,\n" +
                        "    loadtime varchar(200) NOT NULL,\n" +
                        "    bandwidth varchar(200) NOT NULL, \n" +
                        "    date datetime NOT NULL\n" +
                        ");"
        );
        tempBlackList = new HashSet<>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                playerView.setVisibility(View.VISIBLE);
                buttonView.setVisibility(View.GONE);
                getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
                getSpeedTestHostsHandler.start();
            }
        });
        return v;    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        results.setVisibility(View.VISIBLE);
        playerView.setVisibility(View.GONE);
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Util.SDK_INT >= 24) {
            releasePlayer();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    private void initializePlayer() {
        if (player == null) {
            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
            trackSelector = new DefaultTrackSelector(getContext(), trackSelectionFactory);
            trackSelectorParameters = trackSelector.getParameters();
            trackSelector.setParameters(trackSelectorParameters);
            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

            playerView.setPlayer(player);
            // TODO: ADD Playlist
            Uri uri = Uri.parse(getString(R.string.media_url_hls));
            String playerInfo = Util.getUserAgent(getContext(), "ExoPlayerInfo");
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                    getContext(), playerInfo
            );
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(new DefaultExtractorsFactory())
                    .createMediaSource(uri);

            player.prepare(mediaSource);
            player.setPlayWhenReady(playWhenReady);
            player.addAnalyticsListener(new EventLogger(trackSelector));
            player.addAnalyticsListener(new AnalyticsEvents(player, titleTextView, titleTextView1,debugTextView));
            analyticsViewHelper = new AnalyticsTextViewHelper(player, debugTextView);
            analyticsViewHelper.start();
            player.prepare(mediaSource, false, false);
        }
    }

    private MediaSource buildMediaSourceDash(Uri uri) {
        com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), "exoplayer-codelab");
        DashMediaSource.Factory mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
        return mediaSourceFactory.createMediaSource(uri);
    }

    private MediaSource buildMediaSourceHls(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), "exoplayer-codelab");
        HlsMediaSource.Factory mediaSourceFactory = new HlsMediaSource.Factory(dataSourceFactory);
        return mediaSourceFactory.createMediaSource(uri);
    }

    private class PlaybackStateListener implements Player.EventListener {
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    break;
                case ExoPlayer.STATE_ENDED:
                    playerView.setVisibility(View.GONE);
                    results.setVisibility(View.VISIBLE);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String insertSQL = "INSERT INTO video \n" +
                                    "(buffer, loadtime, bandwidth, date)\n" +
                                    "VALUES \n" +
                                    "(?, ?, ?, ?);";
                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                            mDatabase.execSQL(insertSQL, new String[]{debugTextView.getText().toString(),titleTextView1.getText().toString(), titleTextView.getText().toString(), currentDateTimeString});
                            JSONObject resultObj = new JSONObject();
                            try {
                                JSONObject values = new JSONObject();
                                values.put("secLevel", CONFIG.getConfig().getProperty("secLevel"));
                                values.put("filter", CONFIG.getConfig().getProperty("fallbackDNS"));
                                values.put("filterProvider", CONFIG.getConfig().getProperty("filterProvider"));
                                String bufferStr = debugTextView.getText().toString().replaceAll("%\n", "");
                                String loadTimeStr = titleTextView1.getText().toString().replaceAll(" Ms", "");
                                String bandwidthStr = titleTextView.getText().toString().replaceAll("Kbps", "");
                                values.put("buffer", bufferStr);
                                values.put("loadTime", loadTimeStr);
                                values.put("bandwidth", bandwidthStr);
                                resultObj.put("success", true);
                                resultObj.put("taskKey", "USER_INITIATED");
                                resultObj.put("accountName", "Anonymous");
                                resultObj.put("deviceId", PhoneUtils.getPhoneUtils().getDeviceInfo().deviceId);
                                resultObj.put("timestamp", System.currentTimeMillis());
                                resultObj.put("type", "video");
                                resultObj.put("values", values);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            WebSocketConnector.getInstance().sendMessage(Config.STOMP_SERVER_JOB_RESULT_ENDPOINT, resultObj.toString());
                        }});
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d(TAG, "changed state to " + stateString + " playWhenReady: " + playWhenReady);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            analyticsViewHelper.stop();
            player.removeListener(playbackStateListener);
            player.release();
            player = null;

        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }
}
