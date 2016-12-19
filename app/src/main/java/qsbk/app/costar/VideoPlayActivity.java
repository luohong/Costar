//package qsbk.app.costar;
//
//import android.graphics.SurfaceTexture;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.Surface;
//import android.view.TextureView;
//import android.view.View;
//import android.widget.Button;
//
//import qsbk.app.ye.videotools.player.VideoPlayer;
//
//public class VideoPlayActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private static final String TAG = VideoPlayActivity.class.getSimpleName();
//
//    private Video mVideo;
//
//    private TextureView mTextureVideo;
//
//    private Button mBtnPlay;
//
//    private VideoPlayer mVideoPlayer;
//    private Surface mSurface;
//    private int mSurfaceWidth;
//    private int mSurfaceHeight;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_play);
//
//        mVideo = (Video) getIntent().getSerializableExtra("video");
//        if (mVideo.index == 2) {
//            findViewById(R.id.video_1).setVisibility(View.VISIBLE);
//            findViewById(R.id.video_2).setVisibility(View.GONE);
//        } else {
//            findViewById(R.id.video_1).setVisibility(View.GONE);
//            findViewById(R.id.video_2).setVisibility(View.VISIBLE);
//        }
//
//        mTextureVideo = (TextureView) findViewById(R.id.texture_video);
//
//        mBtnPlay = (Button) findViewById(R.id.btn_play);
//        mBtnPlay.setOnClickListener(this);
//        mBtnPlay.setText("播放视频 " + mVideo.index);
//
//        mTextureVideo.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
//            @Override
//            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//                mSurface = new Surface(surface);
//                mSurfaceWidth = width;
//                mSurfaceHeight = height;
//            }
//
//            @Override
//            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//
//            }
//
//            @Override
//            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//                if (mSurface != null) {
//                    mSurface.release();
//                    mSurface = null;
//                }
//                if (mVideoPlayer != null) {
//                    mVideoPlayer.release();
//                    mVideoPlayer = null;
//                }
//                return false;
//            }
//
//            @Override
//            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//            }
//        });
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_play:
//                startPlay();
//                break;
//        }
//    }
//
//    private void startPlay() {
//        if (mVideoPlayer == null) {
//            mVideoPlayer = VideoPlayer.create();
//        }
//        mVideoPlayer.setDataSource(mVideo.path);
//        mVideoPlayer.setSurface(mSurface, mSurfaceWidth, mSurfaceHeight);
//        mVideoPlayer.prepareAsync();
//        mVideoPlayer.setLoop(0);
//        mVideoPlayer.setOnPreparedListener(new VideoPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(VideoPlayer mp) {
//                Log.e(TAG, "onPrepared mp:" + mp);
//                mp.start();
//            }
//        });
//        mVideoPlayer.setOnErrorListener(new VideoPlayer.OnErrorListener() {
//            @Override
//            public void onError(VideoPlayer pc, int what, int extra) {
//                Log.e(TAG, "onError what:" + what + "extra:" + extra);
//            }
//        });
//        mVideoPlayer.setOnInfoListener(new VideoPlayer.OnInfoListener() {
//            @Override
//            public void onInfo(VideoPlayer pc, int what, int extra) {
//                Log.e(TAG, "onInfo what: " + what + ", extra:" + extra);
//            }
//        });
//        mVideoPlayer.setOnCompletionListener(new VideoPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(VideoPlayer mp) {
//                mBtnPlay.setEnabled(true);
//            }
//        });
//    }
//
//
//}
