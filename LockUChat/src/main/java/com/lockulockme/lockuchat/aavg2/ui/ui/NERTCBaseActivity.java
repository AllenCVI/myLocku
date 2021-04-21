package com.lockulockme.lockuchat.aavg2.ui.ui;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.lockulockme.lockuchat.ui.BaseActivity;
import com.lockulockme.lockuchat.utils.LogHelper;

import java.util.List;

public class NERTCBaseActivity extends BaseActivity {
    public boolean moveToFront(Class<?> cls) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningTaskInfo> recentTasks = manager.getRunningTasks(Integer.MAX_VALUE);
            if (recentTasks != null && !recentTasks.isEmpty()) {
                for (ActivityManager.RunningTaskInfo taskInfo : recentTasks) {
                    ComponentName cpn = taskInfo.baseActivity;
                    ComponentName cpn2 = taskInfo.topActivity;
                    LogHelper.e("NERTCBaseActivity",cls.getName()+"---"+cpn.getClassName()+"---"+cpn2.getClassName());
                    if (null != cpn && TextUtils.equals(cls.getName(), cpn.getClassName())) {
                        manager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                        return true;
                    }
                }
            }
        }
        return false;
    }

//    public boolean moveToFront(Class<?> cls)
//    {
//        LogHelper.e("NERTCBaseActivity",""+this.getPackageName());
//        ActivityManager activtyManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(Integer.MAX_VALUE);
//        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos)
//        {
//            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName()))
//            {
//                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
//                return true;
//            }
//        }
//        return false;
//    }

}
