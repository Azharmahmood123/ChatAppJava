package com.java.chat.chatapp.ui.splash;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;

import com.java.chat.chatapp.base.BaseActivity;
import com.java.chat.chatapp.databinding.ActivitySplashBinding;
import com.java.chat.chatapp.ui.dashboard.DashboardActivity;
import com.java.chat.chatapp.ui.login.LoginActivity;
import com.java.chat.chatapp.utils.AppSettings;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity<ActivitySplashBinding> {

    @Override
    protected ActivitySplashBinding getViewBinding() {
        return ActivitySplashBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initializeControls() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding.ivSplashImage.setScaleX(0);
        binding.ivSplashImage.setScaleY(0);

        new Handler().postDelayed(this::initSplashScreen, 400);
    }

    @Override
    public void onBackPressed() {

    }

    private void initSplashScreen() {
        binding.ivSplashImage.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(1000)
                .setInterpolator(new OvershootInterpolator())
                .setStartDelay(200)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        new Handler().postDelayed(() -> {

                            String userId = AppSettings.getUserId();
                            if (TextUtils.isEmpty(userId)) {
                                startSpecificActivity(LoginActivity.class);
                            } else {
                                startSpecificActivity(DashboardActivity.class);
                            }
                            finish();
                        }, 1000);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                }).start();
    }
}
