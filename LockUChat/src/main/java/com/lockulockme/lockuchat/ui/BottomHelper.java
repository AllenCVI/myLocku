package com.lockulockme.lockuchat.ui;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.lockulockme.lockuchat.R;
import com.lockulockme.lockuchat.aavg2.nertcvideocall.utils.ChatAnswerUtils;
import com.lockulockme.lockuchat.bean.rsp.User;
import com.lockulockme.lockuchat.data.SelfDataUtils;
import com.lockulockme.lockuchat.databinding.AcChatBinding;
import com.lockulockme.lockuchat.http.VipDiamondsHelper;
import com.lockulockme.lockuchat.utils.ToastUtils;
import com.lockulockme.lockuchat.utils.permission.OnPermissionsListener;
import com.lockulockme.lockuchat.utils.permission.PermissionsUtils;
import com.lockulockme.lockuchat.view.OnEmojiSelectListener;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;

import java.io.File;

public class BottomHelper {
    private final AcChatBinding binding;
    private final ChatActivity context;
    private final Handler handler;
    private static final int DELAY_SHOW_LAYOUT = 100;
    private AudioRecorder audioMessageHelper;
    private GiftPop giftPop;
    private final User sendUser;
    private static final int HIDEUI = 1001;

    public BottomHelper(AcChatBinding binding, ChatActivity context, Handler handler, User user) {
        this.binding = binding;
        this.context = context;
        this.handler = handler;
        this.sendUser = user;
        initView();
    }
    protected void onResume() {
        if (giftPop!=null){
            giftPop.getDiamondsNum();
        }
    }
    private void initView() {
        binding.rvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideAllUI();
                return false;
            }
        });

        binding.layoutEdit.emojiView.setListener(new OnEmojiSelectListener() {
            @Override
            public void onSelectEmoji(String emoji) {
                inputEmoji(emoji);
            }
        });

        binding.layoutEdit.etMsgContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.scrollToBottom();
                        }
                    }, DELAY_SHOW_LAYOUT);
                }
            }
        });

        binding.layoutEdit.etMsgContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    binding.layoutEdit.llOption.setVisibility(View.GONE);
                    layoutMode = KEYBOARD;
                    showKeyboard();
                }
                return false;
            }
        });
        binding.layoutEdit.etMsgContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.layoutEdit.etMsgContent.getText().toString().length() > 0) {
                    binding.layoutEdit.btnSend.setVisibility(View.VISIBLE);
                    binding.layoutEdit.ivMore.setVisibility(View.GONE);
                } else {
                    binding.layoutEdit.btnSend.setVisibility(View.GONE);
                    binding.layoutEdit.ivMore.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.layoutEdit.btnRecord.setOnTouchListener((v, event) -> {
            if (ChatAnswerUtils.getInstance().isAnswering()){
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    ToastUtils.toastShow(R.string.answer_ing);
                }
                return true;
            }

            PermissionsUtils.getInstance().getPermissions().request(context, new OnPermissionsListener() {
                        @Override
                        public void onResult(boolean granted) {
                            if (granted){
                                handleAudioTouch(v,event);
                            }else {
                                ToastUtils.toastShow(R.string.open_permission_to_chat);
                            }
                        }
                    }, Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return false;
        });
//        binding.rippleBackground.setVisibility("3".equals(sendUser.userType) ? View.GONE : View.VISIBLE);
        initVipUI(SelfDataUtils.getInstance().getSelfData().getSelfIsVip4Sync());

        VipDiamondsHelper.getInstance().getVipStatus(context.toString(), new VipDiamondsHelper.OnVipStatusListener() {
            @Override
            public void onSuccess(int status) {
                initVipUI(status);
            }

            @Override
            public void onFailed() {
                initVipUI(0);
            }
        });
    }

    private void handleAudioTouch(View v,MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touched = true;
            initRecord();
            toStartRecord();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_UP) {
            touched = false;
            toEndRecord(isSlideCancel(v, event));
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            touched = true;
            toCancelRecord(isSlideCancel(v, event));
        }

    }

    public int mVipStatus = -1;
    public void initVipUI(int isVip){
        mVipStatus = isVip;
        binding.rlNoVip.setVisibility(mVipStatus == 1 ? View.GONE : View.VISIBLE);
        binding.layoutEdit.getRoot().setVisibility(mVipStatus == 1 ? View.VISIBLE : View.GONE);
        if (context.chatAdapter.getData().size() == 0) {
            if (mVipStatus == 0) {
                binding.layoutNovip.getRoot().setVisibility(View.VISIBLE);
                binding.layoutWaitForReplay.getRoot().setVisibility(View.GONE);
            }
        }
    }

    public void hideAllUI(){
        hideKeyboard();
        binding.layoutEdit.llOption.setVisibility(View.GONE);
    }

    private boolean touched = false; // 是否按着
    private boolean cancelled = false;
    private boolean started = false;

    // 上滑取消录音判断
    private static boolean isSlideCancel(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        return event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth()
                || event.getRawY() < location[1] - 40;
    }

    private void initRecord() {
        if (audioMessageHelper == null) {
            audioMessageHelper = new AudioRecorder(context, RecordType.AAC, 120, iAudioRecordCallback);
        }
    }

    IAudioRecordCallback iAudioRecordCallback = new IAudioRecordCallback() {
        @Override
        public void onRecordReady() {

        }

        @Override
        public void onRecordStart(File audioFile, RecordType recordType) {
            started = true;
            if (!touched) {
                return;
            }

            binding.tvRecordHint.setText(R.string.release_to_end);
            binding.tvRecordHint.setAlpha(.8f);

            updateTimerHint(false); // 初始化语音动画状态
            showRecordView();
        }

        @Override
        public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
            context.sendVoiceMsg(audioFile, audioLength);
        }

        @Override
        public void onRecordFail() {
            if (started) {
                ToastUtils.toastShow(R.string.record_failed);
            }
        }

        @Override
        public void onRecordCancel() {

        }

        @Override
        public void onRecordReachedMaxTime(final int maxTime) {
            hideRecordView();
            audioMessageHelper.handleEndRecord(true, maxTime);
        }
    };

    /**
     * 开始录制
     */
    private void toStartRecord() {
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        audioMessageHelper.startRecord();
        cancelled = false;
    }

    /**
     * 结束录制
     */
    private void toEndRecord(boolean cancel) {
        started = false;
        context.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        audioMessageHelper.completeRecord(cancel);
        binding.layoutEdit.btnRecord.setText(R.string.hold_and_talk);
        binding.layoutEdit.btnRecord.setAlpha(1f);
        hideRecordView();
    }

    /**
     * 取消录制
     */
    private void toCancelRecord(boolean cancel) {
        // reject
        if (!started) {
            return;
        }
        // no change
        if (cancelled == cancel) {
            return;
        }

        cancelled = cancel;
        updateTimerHint(cancel);
    }

    /**
     * 正在进行语音录制和取消语音录制，界面展示
     */
    private void updateTimerHint(boolean cancel) {
        if (cancel) {
            binding.tvRecordHint.setText(R.string.release_to_end);
            binding.tvRecordHint.setTextColor(Color.RED);
        } else {
            binding.tvRecordHint.setText(R.string.release_send);
            binding.tvRecordHint.setTextColor(Color.parseColor("#99ffffff"));
        }
    }

    /**
     * 开始显示录制页面
     */
    private void showRecordView() {
        binding.llRecordVoice.setVisibility(View.VISIBLE);
        binding.tvRecordTime.setBase(SystemClock.elapsedRealtime());
        binding.tvRecordTime.start();
    }

    /**
     * 结束显示录制页面
     */
    private void hideRecordView() {
        binding.llRecordVoice.setVisibility(View.GONE);
        binding.tvRecordTime.stop();
        binding.tvRecordTime.setBase(SystemClock.elapsedRealtime());
    }

    private void inputEmoji(String emoji) {
        Editable mEditable = binding.layoutEdit.etMsgContent.getText();
        if (emoji.equals("/delete")) {
            binding.layoutEdit.etMsgContent.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            int start = binding.layoutEdit.etMsgContent.getSelectionStart();
            int end = binding.layoutEdit.etMsgContent.getSelectionEnd();
            start = (start < 0 ? 0 : start);
            end = (start < 0 ? 0 : end);
            mEditable.replace(start, end, emoji);
        }
    }


    public void onBtnClick(View view) {
        if (view.getId() == R.id.iv_emoji_face) {
            switchEmojiKeyboard();
        } else if (view.getId() == R.id.iv_more) {
            switchMoreKeyboard();
        } else if (view.getId() == R.id.iv_gift) {
            showGiftPop();
        }
    }

    private void showGiftPop() {
        hideKeyboard();
        if (giftPop == null)
            giftPop = new GiftPop(context, context, context.sessionType, context.sendUser);

        giftPop.show(binding.getRoot());
    }

    private void switchMoreKeyboard() {
        if (layoutMode == MORE) {
            layoutMode = KEYBOARD;
            binding.layoutEdit.llMore.setVisibility(View.GONE);
            showKeyboard();
        } else {
            layoutMode = MORE;
            showMore();
        }
    }

    private void showMore() {
        hideKeyboard();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.layoutEdit.llOption.setVisibility(View.VISIBLE);
                binding.layoutEdit.emojiView.setVisibility(View.GONE);
                binding.layoutEdit.llMore.setVisibility(View.VISIBLE);
                binding.layoutEdit.llMsgContent.setVisibility(View.VISIBLE);
                binding.layoutEdit.btnRecord.setVisibility(View.GONE);
                binding.layoutEdit.etMsgContent.clearFocus();
            }
        }, DELAY_SHOW_LAYOUT);
    }

    private void switchEmojiKeyboard() {
        if (layoutMode == EMOJI) {
            layoutMode = KEYBOARD;
            binding.layoutEdit.emojiView.setVisibility(View.GONE);
            showKeyboard();
        } else {
            layoutMode = EMOJI;
            showEmoji();
        }
    }

    private void showEmoji() {
        hideKeyboard();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.layoutEdit.llOption.setVisibility(View.VISIBLE);
                binding.layoutEdit.llMore.setVisibility(View.GONE);
                binding.layoutEdit.emojiView.setVisibility(View.VISIBLE);
                binding.layoutEdit.etMsgContent.requestFocus();
            }
        }, DELAY_SHOW_LAYOUT);
    }

    private static final int DEFAULT = 0;
    private static final int KEYBOARD = 1;
    private static final int VOICE = 2;
    private static final int EMOJI = 3;
    private static final int MORE = 4;
    int layoutMode = DEFAULT;

    public void switchVoiceKeyboard() {
        if (layoutMode == VOICE) {
            layoutMode = KEYBOARD;
            binding.layoutEdit.ivRecordVoice.setImageResource(R.mipmap.icon_voice_chat_bottom);
            binding.layoutEdit.llMsgContent.setVisibility(View.VISIBLE);
            binding.layoutEdit.btnRecord.setVisibility(View.GONE);
            binding.layoutEdit.etMsgContent.requestFocus();
            showKeyboard();
        } else {
            layoutMode = VOICE;
            binding.layoutEdit.ivRecordVoice.setImageResource(R.mipmap.icon_keyboard_chat);
            binding.layoutEdit.btnRecord.setVisibility(View.VISIBLE);
            binding.layoutEdit.llMsgContent.setVisibility(View.GONE);
            hideKeyboard();
            binding.layoutEdit.llOption.setVisibility(View.GONE);
        }
    }

    private boolean isKeyboardShowed = true; // 是否显示键盘

    // 隐藏键盘布局
    public void hideKeyboard() {
        isKeyboardShowed = false;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.layoutEdit.etMsgContent.getWindowToken(), 0);
        binding.layoutEdit.etMsgContent.clearFocus();
    }

    // 显示键盘布局
    private void showKeyboard() {
        handler.postDelayed(showKeyboardRunnable, DELAY_SHOW_LAYOUT);
    }

    private final Runnable showKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            binding.layoutEdit.etMsgContent.requestFocus();
            if (!isKeyboardShowed) {
                binding.layoutEdit.etMsgContent.setSelection(binding.layoutEdit.etMsgContent.getText().length());
                isKeyboardShowed = true;
            }
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.layoutEdit.etMsgContent, 0);
            binding.layoutEdit.llOption.setVisibility(View.GONE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.scrollToBottom();
                }
            }, 200);

        }
    };
}
