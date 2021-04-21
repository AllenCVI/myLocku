package com.lockulockme.lockuchat.utils;

import android.media.AudioManager;

import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;

public class AudioMsgPlayer implements OnPlayListener {
    private static AudioMsgPlayer audioMsgPlayer;
    private AudioPlayer audioPlayer;
    private boolean isEar;
    private String filePath;
    private OnPlayListener onPlayListener;

    private AudioMsgPlayer() {
        audioPlayer = new AudioPlayer(ContextHelper.getInstance().getContext());
        audioPlayer.setOnPlayListener(this);
    }

    public static AudioMsgPlayer getInstance() {
        if (audioMsgPlayer == null) {
            synchronized (AudioMsgPlayer.class) {
                if (audioMsgPlayer == null) {
                    audioMsgPlayer = new AudioMsgPlayer();
                }
            }
        }
        return audioMsgPlayer;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isPlaying(){
        return audioPlayer.isPlaying();
    }

    private void setAudioFilePath(String file){
        audioPlayer.setDataSource(file);
    }

    public boolean isSameMsg(String path){
        if (filePath!=null&&filePath.equals(path)){
            return true;
        }else {
            return false;
        }
    }

    public void playAudio(String file){
        if (filePath==null){
            start(file);
        }else {
            if (filePath.equals(file)){
                if (audioPlayer.isPlaying()){
                    stop();
                }else {
                    start(file);
                }
            }else {
                stop();
                start(file);
            }
        }
    }

    private void start(String file){
        filePath=file;
        setAudioFilePath(file);
        start();
    }
    private void start(){
        if (!audioPlayer.isPlaying()){
            audioPlayer.start(getPlayStreamType());
        }
    }

    public void stop(){
        if (audioPlayer !=null && audioPlayer.isPlaying()){
            audioPlayer.stop();
            if (onPlayListener!=null){
                onPlayListener.onPlayend(audioPlayer.getDuration());
            }
        }
    }

    private int getPlayStreamType(){
        // 听筒模式/扬声器模式
        if (isEar) {
            return AudioManager.STREAM_VOICE_CALL;
        } else {
            return AudioManager.STREAM_MUSIC;
        }
    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onCompletion() {
        if (onPlayListener!=null){
            onPlayListener.onPlayend(audioPlayer.getDuration());
        }
    }

    @Override
    public void onInterrupt() {
        if (onPlayListener!=null){
            onPlayListener.onPlayend(audioPlayer.getDuration());
        }
    }

    @Override
    public void onError(String s) {
        if (onPlayListener!=null){
            onPlayListener.onPlayend(audioPlayer.getDuration());
        }
    }

    @Override
    public void onPlaying(long l) {
        if (onPlayListener!=null){
            onPlayListener.onPlaying(l,audioPlayer.getDuration());
        }
    }

    public interface OnPlayListener{
        void onPlaying(long pos,long duration);
        void onPlayend(long duration);
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }
}
