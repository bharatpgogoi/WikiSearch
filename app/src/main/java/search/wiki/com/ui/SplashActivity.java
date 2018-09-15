package search.wiki.com.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import java.lang.ref.WeakReference;

import search.wiki.com.wikisearch.R;

public class SplashActivity extends BaseActivity {

    private SplashHandler mSplashHandler;
    private static final String TAG = "Wiki.SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashHandler = new SplashHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSplashHandler.sendEmptyMessage(0);
    }

    private static class SplashHandler extends Handler {

        private static final int MESSAGE_FINISH = 1;
        private final WeakReference<SplashActivity> mActivityRef;

        SplashHandler (SplashActivity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "timeout elapsed");
            super.handleMessage(msg);

            if (msg.what == MESSAGE_FINISH) {
                Log.i(TAG, "finishing");
                if (mActivityRef.get() != null) {
                    SplashActivity splashActivity = mActivityRef.get();
                    splashActivity.launchSearchActivity();
                }
                return;
            }
            sendEmptyMessageDelayed(MESSAGE_FINISH, 3000);
        }

    }
    @Override
    public void onBackPressed() {

    }
}
