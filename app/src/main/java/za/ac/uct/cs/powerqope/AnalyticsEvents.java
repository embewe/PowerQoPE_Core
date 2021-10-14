/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package za.ac.uct.cs.powerqope;

import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.util.Assertions;

/**
 * A helper class for tracking Events with information obtained from a {@link AnalyticsListener}.
 */
public class AnalyticsEvents implements AnalyticsListener {

    private static final String TAG = PlayerActivity.class.getName();
    private final SimpleExoPlayer player;
    private final TextView textView, textView1, debugTextView;
    private int counter = 0;

    public AnalyticsEvents(SimpleExoPlayer player, TextView textView, TextView textView1, TextView debugTextView) {
        Assertions.checkArgument(player.getApplicationLooper() == Looper.getMainLooper());
        this.player = player;
        this.textView = textView;
        this.textView1 = textView1;
        this.debugTextView = debugTextView;
    }

    @Override
    public void onVideoSizeChanged(
            EventTime eventTime,
            int width,
            int height,
            int unappliedRotationDegrees,
            float pixelWidthHeightRatio) {
        Log.d(TAG, "ANALYTICS EVENT: " + "videoSize: " +  width + ", " + height);
        counter++;

    }

    @Override
    public void onBandwidthEstimate(
            EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
        // Do nothing.
        Log.d(TAG, "ANALYTICS BANDWIDTH EVENT: " + eventTime + "total Load Time: "
                + totalLoadTimeMs + ", total Bytes Loaded: " + totalBytesLoaded
        + ",  bitrateEstimate: " + bitrateEstimate);
        debugTextView.setText(player.getBufferedPercentage() + "%\n");
        textView.setText( bitrateEstimate/1024 + "Kbps");
        textView1.setText(totalLoadTimeMs +" Ms\n");

    }
}
