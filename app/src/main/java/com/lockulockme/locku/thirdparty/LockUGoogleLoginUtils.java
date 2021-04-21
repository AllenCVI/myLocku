package com.lockulockme.locku.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.lockulockme.locku.base.utils.LogUtil;

public class LockUGoogleLoginUtils {
    private LockUGoogleLoginUtils(){};
    private static volatile LockUGoogleLoginUtils instance;

    public static LockUGoogleLoginUtils getInstance(){
        if (instance==null){
            synchronized (LockUGoogleLoginUtils.class){
                if (instance==null){
                    instance = new LockUGoogleLoginUtils();
                }
            }
        }
        return instance;
    }


    public void LoginToGoogle(Activity context,GoogleSignInClient googleSignInClient, int code) {
        Intent loginIntent = googleSignInClient.getSignInIntent();
        context.startActivityForResult(loginIntent, code);
    }


    public void onResult(int requestCode, Intent data, int code, GoogleLoginListener googleLoginListener) {
        if (requestCode == code) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(googleSignInAccountTask, googleLoginListener);
        }
    }


    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> googleSignInAccountTask,
                                           GoogleLoginListener googleLoginListener) {
        try {
            GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);
            String idToken = googleSignInAccount.getIdToken();
            googleLoginListener.successful(idToken);
            LogUtil.LogE("LockUGoogleLoginUtils", idToken);
        } catch (Exception e) {
            googleLoginListener.failed();
        }
    }


    public void GoogleSingOut(Activity context,GoogleSignInClient googleSignInClient) {
        googleSignInClient.signOut().addOnCompleteListener(context, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                LogUtil.LogE("LockUGoogleLoginUtils", "loginOutSuccess");
            }
        });
    }

}
