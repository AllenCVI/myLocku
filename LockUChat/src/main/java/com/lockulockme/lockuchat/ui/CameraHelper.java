package com.lockulockme.lockuchat.ui;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;

public class CameraHelper implements SurfaceHolder.Callback {

    private static final String TAG = "CameraHelper";
    private int mCameraId;
    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;

    public CameraHelper() {
        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        stopPreview();
        startPreview();
    }

    private void stopPreview() {
        if (mCamera != null) {
            //预览数据回调接口
            mCamera.setPreviewCallback(null);
            //停止预览
            mCamera.stopPreview();
            //释放摄像头
            mCamera.release();
            mCamera = null;
        }
    }

    private void startPreview() {
        try {
            //获得camera对象
            mCamera = Camera.open(mCameraId);
            //配置camera的属性
            Camera.Parameters parameters = mCamera.getParameters();
            //设置预览数据格式为nv21
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
            //设置预览画面
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //释放摄像头
        stopPreview();
        //开启摄像头
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
    }


    public void release() {
        mSurfaceHolder.removeCallback(this);
        stopPreview();
    }
}
