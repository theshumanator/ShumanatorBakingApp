package com.example.fatoumeh.shumanatorbakingapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.ButterKnife;

/**
 * Created by fatoumeh on 08/06/2018.
 */

public class RecipeStepsVideoFragment extends Fragment implements ExoPlayer.EventListener {

    private static final String TAG = RecipeStepsVideoFragment.class.getSimpleName();
    private String videoUrl;
    private Long playbackPos;
    private boolean playbackState;
    private SimpleExoPlayer videoPlayer;
    private SimpleExoPlayerView videoPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    public RecipeStepsVideoFragment() {
    }

    /*
        exoplayer code: some of these were copied from the exoplayer lesson with minor tweaks
        no need for notifications because we dont get user to use the app in background
    */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_video, container, false);
        videoPlayerView=ButterKnife.findById(rootView, R.id.exo_video_player);

        initialiseMediaSession();

        //Here we need to check the savedInstance bundle for any data we'd saved on rotating the device
        if(savedInstanceState != null) {
            videoUrl=savedInstanceState.getString(getString(R.string.json_videoURL));
            playbackPos=savedInstanceState.getLong(getString(R.string.playback_position));
            playbackState=savedInstanceState.getBoolean(getString(R.string.playback_state));
            initialisePlayer(Uri.parse(videoUrl), playbackPos, playbackState);
        } else {
            initialisePlayer(Uri.parse(videoUrl), 0l, true);
        }
        return rootView;
    }

    private void initialiseMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void initialisePlayer(Uri videoUri, long playbackPos, boolean playbackState) {
        if (videoPlayer == null) {

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            videoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            videoPlayerView.setPlayer(videoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            videoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            videoPlayer.prepare(mediaSource);
            if (playbackPos>0l) {
                videoPlayer.seekTo(playbackPos);
            }

            videoPlayer.setPlayWhenReady(playbackState);
        }
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (videoPlayer!=null) {
            videoPlayer.stop();
            videoPlayer.release();
            videoPlayer = null;
            mMediaSession.setActive(false);
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.json_videoURL), videoUrl);
        outState.putLong(getString(R.string.playback_position), videoPlayer.getCurrentPosition());
        outState.putBoolean(getString(R.string.playback_state), videoPlayer.getPlayWhenReady());
    }

    private void releasePlayer() {
        if (videoPlayer != null) {
            playbackState = videoPlayer.getPlayWhenReady();
            playbackPos = videoPlayer.getCurrentPosition();
            videoPlayer.release();
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    videoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    videoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            videoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            videoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            videoPlayer.seekTo(0);
        }
    }
}
