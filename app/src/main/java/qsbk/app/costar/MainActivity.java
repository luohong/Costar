package qsbk.app.costar;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQEUST_CODE_VIDEO_RECORD = 1001;

    private Handler mHandler;
    private Video mVideo1;
    private Video mVideo2;

    private TextureView mTextureVideo1;
    private TextureView mTextureVideo2;

    private View mMaskVideo1;
    private View mMaskVideo2;

    private Button mBtnRecord1;
    private Button mBtnRecord2;
    private Button mBtnDone;

    private MediaPlayer mVideoPlayer1;
    private Surface mSurface1;
    private int mSurface1Width;
    private int mSurface1Height;

    private MediaPlayer mVideoPlayer2;
    private Surface mSurface2;
    private int mSurface2Width;
    private int mSurface2Height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();
        mVideo1 = new Video(1);
        mVideo2 = new Video(2);

        mTextureVideo1 = (TextureView) findViewById(R.id.texture_video_1);
        mTextureVideo2 = (TextureView) findViewById(R.id.texture_video_2);

        mMaskVideo1 = findViewById(R.id.mask_video_1);
        mMaskVideo2 = findViewById(R.id.mask_video_2);

        mBtnRecord1 = (Button) findViewById(R.id.btn_record_1);
        mBtnRecord2 = (Button) findViewById(R.id.btn_record_2);
        mBtnDone = (Button) findViewById(R.id.btn_done);

        mBtnRecord1.setOnClickListener(this);
        mBtnRecord2.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);

        mMaskVideo1.setVisibility(View.GONE);
        mBtnDone.setVisibility(View.GONE);

        mVideoPlayer1 = new MediaPlayer();
        mVideoPlayer2 = new MediaPlayer();

        mTextureVideo1.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurface1 = new Surface(surface);
                mSurface1Width = width;
                mSurface1Height = height;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (mSurface1 != null) {
                    mSurface1.release();
                    mSurface1 = null;
                }
                if (mVideoPlayer1 != null) {
                    mVideoPlayer1.release();
                    mVideoPlayer1 = null;
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        mTextureVideo2.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurface2 = new Surface(surface);
                mSurface2Width = width;
                mSurface2Height = height;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (mSurface2 != null) {
                    mSurface2.release();
                    mSurface2 = null;
                }
                if (mVideoPlayer2 != null) {
                    mVideoPlayer2.release();
                    mVideoPlayer2 = null;
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.tv_record_hint).setVisibility(View.GONE);
            }
        }, 5000);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record_1:
                mBtnRecord1.setEnabled(false);
                if (!mVideo1.recorderCompleted) {
                    startRecord(mVideo1);
                } else {
                    startPlay(mVideo1, mVideoPlayer1);
                }
                break;
            case R.id.btn_record_2:
                mBtnRecord2.setEnabled(false);
                if (!mVideo2.recorderCompleted) {
                    startRecord(mVideo2);
                } else {
                    startPlay(mVideo2, mVideoPlayer2);
                }
                break;
            case R.id.btn_done:
                startCostar();
                break;
        }
    }

    private void startCostar() {
        startPlay(mVideo1, mVideoPlayer1);
        startPlay(mVideo2, mVideoPlayer2);
    }

    private void startRecord(final Video video) {
        Intent intent = new Intent();
        intent.setClass(this, VideoRecordActivity.class);
        intent.putExtra("video", video);
        startActivityForResult(intent, REQEUST_CODE_VIDEO_RECORD);
    }

    private void startPlay(final Video video, MediaPlayer videoPlayer) {
        try {
            videoPlayer.setDataSource(video.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (video.index == 1) {
            videoPlayer.setSurface(mSurface1);
        } else if (video.index == 2) {
            videoPlayer.setSurface(mSurface2);
        }
        videoPlayer.prepareAsync();
//        videoPlayer.setLoop(0);
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e(TAG, "onPrepared mp:" + mp);
                mp.start();
            }
        });
        videoPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer pc, int what, int extra) {
                Log.e(TAG, "onError what:" + what + "extra:" + extra);
                return false;
            }
        });
        videoPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer pc, int what, int extra) {
                Log.e(TAG, "onInfo what: " + what + ", extra:" + extra);
                return false;
            }
        });
        videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Button btnRecord = video.index == 1 ? mBtnRecord1 : mBtnRecord2;
                btnRecord.setEnabled(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQEUST_CODE_VIDEO_RECORD == requestCode) {
            mBtnRecord1.setEnabled(true);

            if (resultCode == RESULT_OK && data != null) {
                Video video = (Video) data.getSerializableExtra("video");
                if (video != null) {
                    if (video.index == mVideo1.index) {
                        mVideo1 = video;
                        startPlay(mVideo1, mVideoPlayer1);
                    } else if (video.index == mVideo2.index) {
                        mVideo2 = video;
                        startPlay(mVideo2, mVideoPlayer2);
                    }
                }
            }

            if (mVideo1.recorderCompleted) {
                mMaskVideo2.setVisibility(View.VISIBLE);
                mBtnRecord2.setEnabled(true);
                mBtnRecord1.setText("播放视频 " + mVideo1.index);
            } else if (mVideo2.recorderCompleted) {
                mBtnDone.setVisibility(View.VISIBLE);
                mBtnRecord1.setText("播放视频 " + mVideo2.index);
            }
        }
    }
}
