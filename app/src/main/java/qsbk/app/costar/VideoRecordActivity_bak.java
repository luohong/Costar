//package qsbk.app.costar;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.TextureView;
//import android.view.View;
//import android.widget.Button;
//
//import qsbk.app.ye.videotools.camera.CameraHelper;
//import qsbk.app.ye.videotools.camera.CameraLoader;
//import qsbk.app.ye.videotools.camera.CameraRender;
//import qsbk.app.ye.videotools.recorder.MediaRecorder;
//
//public class VideoRecordActivity_bak extends AppCompatActivity implements View.OnClickListener, CameraRender.SurfaceListener {
//
//    private static final String TAG = VideoRecordActivity_bak.class.getSimpleName();
//    private static final int MAX_RECORD_TIME = 5 * 1000;
//
//    private Handler mHandler;
//    private Video mVideo;
//
//    private TextureView mTextureVideo;
//
//    private Button mBtnRecord;
//
//    private CameraHelper mCameraHelper;
//    private CameraRender mCameraRenderer;
//    private CameraLoader mCameraLoader;
//    private MediaRecorder mRecorder;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_record);
//
//        mHandler = new Handler();
//        mVideo = (Video) getIntent().getSerializableExtra("video");
//        mTextureVideo = (TextureView) findViewById(R.id.texture_video);
//
//        mBtnRecord = (Button) findViewById(R.id.btn_record);
//        mBtnRecord.setOnClickListener(this);
//        mBtnRecord.setText("拍摄视频 " + mVideo.index);
//
//        mCameraRenderer = new CameraRender(this);
//        mTextureVideo.setSurfaceTextureListener(mCameraRenderer);
//        mCameraRenderer.setTextureView(mTextureVideo);
//
//        mCameraHelper = new CameraHelper(this);
//        int backCameraId = mCameraHelper.hasBackCamera();
//        if (backCameraId == -1) {
//            backCameraId = 0;
//        }
//        mCameraLoader = new CameraLoader(backCameraId, mCameraHelper, mCameraRenderer, getMainLooper());
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                findViewById(R.id.tv_record_hint).setVisibility(View.GONE);
//            }
//        }, 5000);
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_record:
//                startRecord();
//                break;
//        }
//    }
//
//    private void startRecord() {
//        if (mRecorder == null) {
//            mRecorder = MediaRecorder.create();
//            mRecorder.setOutputFile(mVideo.path);
//            mRecorder.maxRecordTime(MAX_RECORD_TIME);
//            mRecorder.setRate(1, 1);
//            mRecorder.setFramesPerSecond(24);
//            mRecorder.setDimension(mCameraRenderer.getImageWidth(), mCameraRenderer.getImageHeight());
//            mRecorder.prepare();
//            mRecorder.start();
//
//            mCameraRenderer.setSink(mRecorder);
//
//            mRecorder.setOnCompletionListener(new MediaRecorder.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaRecorder mr) {
//                    Log.d(TAG, "recorder on totally complete");
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mVideo.recorderCompleted = true;
//                            stopRecord(false);
//                            closeCamera();
//
//                            Intent data = new Intent();
//                            data.putExtra("video", mVideo);
//                            setResult(RESULT_OK, data);
//
////                            Intent intent = new Intent();
////                            intent.setClass(getApplicationContext(), VideoPlayActivity.class);
////                            intent.putExtra("video", mVideo);
////                            startActivity(intent);
//
//                            finish();
//                        }
//                    });
//                }
//            });
//        }
//    }
//
//    protected void stopRecord(boolean quickStop) {
//        if (mRecorder != null) {
//            mCameraRenderer.setSink(null);
//            mRecorder.stop(quickStop);
//            mRecorder.release();
//            mRecorder = null;
//        }
//    }
//
//    @Override
//    public void onSurfaceTextureAvailable() {
//        openCamera();
//    }
//
//    @Override
//    public void onSurfaceTextureDestroyed() {
//        closeCamera();
//        stopRecord(false);
//    }
//
//    private void openCamera() {
//        if (mCameraLoader != null) {
//            mCameraLoader.setUpCamera();
//        }
//    }
//
//    private void closeCamera() {
//        if (mCameraLoader != null) {
//            mCameraLoader.releaseCamera();
//        }
//    }
//
//}
