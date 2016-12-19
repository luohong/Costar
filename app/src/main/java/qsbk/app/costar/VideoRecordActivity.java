package qsbk.app.costar;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

public class VideoRecordActivity extends AppCompatActivity implements View.OnClickListener, TextureView.SurfaceTextureListener {

    private static final String TAG = VideoRecordActivity.class.getSimpleName();
    private static final int MAX_RECORD_TIME = 5 * 1000;

    private Handler mHandler;
    private Video mVideo;

    private TextureView mTextureVideo;

    private Button mBtnRecord;

    private Camera mCamera;
    private MediaRecorder mRecorder;
    private SurfaceTexture mSurfaceTexture;
    private int mWidth;
    private int mHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);

        mHandler = new Handler();
        mVideo = (Video) getIntent().getSerializableExtra("video");
        mTextureVideo = (TextureView) findViewById(R.id.texture_video);
        mTextureVideo.setSurfaceTextureListener(this);

        mBtnRecord = (Button) findViewById(R.id.btn_record);
        mBtnRecord.setOnClickListener(this);
        mBtnRecord.setText("拍摄视频 " + mVideo.index);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.tv_record_hint).setVisibility(View.GONE);
            }
        }, 5000);

    }

    private void initCamera(Camera camera) {
        if (camera == null) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        //请通过parameters.getSupportedPreviewSizes();设置预览大小,否则设置了一个摄像头不支持大小,将会报错.
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size bestSize = null;
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);
            Log.e("camerasize", size.width + ":" + size.height);
            if (bestSize == null) {
                bestSize = size;
            } else if (Math.abs((bestSize.width - mWidth) + (bestSize.height - mHeight)) <
                    Math.abs((size.width - mWidth) + (size.height - mHeight))) {
                bestSize = size;
            }
        }
        Log.e("camera", mWidth + ":" + mHeight);
        parameters.setPreviewSize(bestSize.width, bestSize.height);//如果设置了一个不支持的大小,会崩溃.坑2

        //请通过parameters.getSupportedPictureSizes();设置拍照图片大小,这一步对于录像来说是非必须的.
        //parameters.setPictureSize(1920, 1080);
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            //设置对焦模式
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        camera.setParameters(parameters);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record:
                startRecord();
                break;
        }
    }

    private void createMediaRecorder() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
    }

    private void startRecord() {
        if (mCamera == null) {
            return;
        }

        try {
            mCamera.unlock();//注意处
        } catch (Exception e) {
            Log.e("angcyo", e.getMessage());
            return;
        }

        createMediaRecorder();

        mRecorder.reset();//尽可能在调用其他方法之前,调用reset方法.避免状态异常调用.坑4
        mRecorder.setCamera(mCamera);//注意顺序,请注意此方法调用的顺序.调用顺序错了,会崩溃

        // 设置录制视频源为Camera(相机)
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置录制的视频编码h263 h264
//        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);

        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mRecorder.setProfile(camcorderProfile);
        // 设置视频文件输出的路径
        mRecorder.setOutputFile(mVideo.path);
        mRecorder.setMaxDuration(MAX_RECORD_TIME);
//        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
//        mRecorder.setVideoSize(176, 144);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
//        mRecorder.setVideoFrameRate(24);
//        mRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        try {
            // 准备录制
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 开始录制
        mRecorder.start();

        mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mediaRecorder, int what, int i1) {
                Log.e("luohong", "onError " + what);
            }
        });
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int what, int i1) {
                Log.e("luohong", "onInfo " + what);
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "recorder on totally complete");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mVideo.recorderCompleted = true;
                        stopRecord();
                        closeCamera();

                        Intent data = new Intent();
                        data.putExtra("video", mVideo);
                        setResult(RESULT_OK, data);

//                            Intent intent = new Intent();
//                            intent.setClass(getApplicationContext(), VideoPlayActivity.class);
//                            intent.putExtra("video", mVideo);
//                            startActivity(intent);

                        finish();
                    }
                });
            }
        }, MAX_RECORD_TIME);
    }

    protected void stopRecord() {
        if (mRecorder != null) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void openCamera() {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);//注意处,如果没有设置preview,录像会报错
        } catch (IOException e) {
            e.printStackTrace();
        }
        initCamera(mCamera);
    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        mSurfaceTexture = surfaceTexture;
        mWidth = width;
        mHeight = height;
        openCamera();
        createMediaRecorder();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        closeCamera();
        stopRecord();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
