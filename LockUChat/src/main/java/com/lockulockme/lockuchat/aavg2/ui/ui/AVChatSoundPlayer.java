package com.lockulockme.lockuchat.aavg2.ui.ui;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Vibrator;

import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.model.NERTCVideoCall;
import com.lockulockme.lockuchat.utils.ContextHelper;

public class AVChatSoundPlayer {

    public enum RingerTypeEnum {
        CONNECTING,
        NO_RESPONSE,
        PEER_BUSY,
        PEER_REJECT,
        RING,;
    }

    private Context context;

    private SoundPool soundPool;
    private AudioManager audioManager;
    private int streamId;
    private int soundId;
    private boolean loop;
    private RingerTypeEnum ringerTypeEnum;
    private boolean isRingModeRegister = false;
    private int ringMode = -1;

    private static AVChatSoundPlayer instance = null;
    private RingModeChangeReceiver ringModeChangeReceiver;

    public static AVChatSoundPlayer instance() {
        if (instance == null) {
            synchronized (AVChatSoundPlayer.class) {
                if (instance == null) {
                    instance = new AVChatSoundPlayer();
                }
            }
        }
        return instance;
    }

    public AVChatSoundPlayer() {
        this.context = ContextHelper.getInstance().getContext();
    }

    public synchronized void play(RingerTypeEnum type) {
        this.ringerTypeEnum = type;
        int ringId = 0;
        switch (type) {
            case CONNECTING:
                ringId = R.raw.connecting;
                loop = true;
                break;
            case RING:
//                ringId = R.raw.avchat_ring;
                showSystemDefaultRing(true);
                loop = true;
                break;
            default:
                stop();
                break;
        }

        if (ringId != 0) {
            play(ringId);
        }

    }

    public void stop() {
        try {
            showSystemDefaultRing(false);
            if (soundPool != null) {
                if (streamId != 0) {
                    soundPool.stop(streamId);
                    streamId = 0;
                }
                if (soundId != 0) {
                    soundPool.unload(soundId);
                    soundId = 0;
                }
            }
            if (isRingModeRegister) {
                registerVolumeReceiver(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void play(int ringId) {
        initSoundPool();
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            soundId = soundPool.load(context, ringId, 1);
        }
    }

    private void initSoundPool() {
        try {
            stop();
            if (soundPool == null) {
                initSoundPoolByVersion();
                soundPool.setOnLoadCompleteListener(onLoadCompleteListener);
                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                ringMode = audioManager.getRingerMode();
            }
            registerVolumeReceiver(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initSoundPoolByVersion() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
    }

    SoundPool.OnLoadCompleteListener onLoadCompleteListener = new SoundPool.OnLoadCompleteListener() {
        @Override
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            if (soundId != 0 && status == 0) {
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    streamId = soundPool.play(soundId, curVolume, curVolume, 1, loop ? -1 : 0, 1f);
                }
            }
        }
    };

    private void registerVolumeReceiver(boolean register) {
        try {
            if (ringModeChangeReceiver == null) {
                ringModeChangeReceiver = new RingModeChangeReceiver();
            }

            if (register) {
                isRingModeRegister = true;
                IntentFilter filter = new IntentFilter();
                filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
                context.registerReceiver(ringModeChangeReceiver, filter);
            } else {
                context.unregisterReceiver(ringModeChangeReceiver);
                isRingModeRegister = false;
            }
        } catch (Exception e) {

        }

    }

    private class RingModeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ringMode != -1 && ringMode != audioManager.getRingerMode()
                    && intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                ringMode = audioManager.getRingerMode();
                play(ringerTypeEnum);
            }
        }
    }

    public void showSystemDefaultRing(boolean isStart) {
        try {
            if (context==null) return;
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int ringerMode = audioManager.getRingerMode();
            switch (ringerMode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    startSystemLongRing(isStart);
                    startLongVibrate(isStart);
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    startLongVibrate(isStart);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private MediaPlayer mediaPlayer;

    public void startSystemLongRing(boolean isStart) {
        try {
            if (isStart) {
                Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
                if (uri==null) return;
                mediaPlayer = MediaPlayer.create(context, uri);
                if (mediaPlayer == null) {
                    return;
                }
                mediaPlayer.setLooping(true);
                try {
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            } else if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Vibrator vibrator;

    /**
     * 15s震动
     */
    public void startLongVibrate(boolean isStart) {
        if (isStart) {
            vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            if (vibrator.hasVibrator()) {
                long[] longs = new long[60];
                for (int i=0;i<60;i++) {
                    longs[i] = i%2 == 0 ? 1000 : 1400;
                }
                vibrator.vibrate(longs, 0);
            }
        } else if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

}
