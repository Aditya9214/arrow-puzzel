package com.painfullgames.freepath;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends Activity {
    // Keep true while developing. Google explicitly recommends test ads during development.
    // Before release, set to false after testing and verifying your AdMob setup.
    private static final boolean USE_TEST_ADS = true;
    private static final String TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String PRODUCTION_REWARDED_ID = "ca-app-pub-5458302282891714/8631947211";

    private WebView webView;
    private RewardedAd rewardedAd;
    private boolean adShowing = false;
    // "heart" or "hint": what the earned reward is for.
    private String pendingPurpose = "heart";
    private int safeTop = 0;
    private int safeBottom = 0;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(0xFF090A12));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(attrs);
        }

        webView = new WebView(this);
        webView.setBackgroundColor(0xFF090A12);
        setContentView(webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                applySafeArea();
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(false);
        // Respect the page's viewport meta tag so it fills the phone width.
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setOnApplyWindowInsetsListener((v, insets) -> {
            readSafeArea(insets);
            return insets;
        });
        webView.addJavascriptInterface(new AdsBridge(), "AndroidAds");

        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        enterFullScreen();
        webView.loadUrl("file:///android_asset/index.html");

        MobileAds.initialize(this, initializationStatus -> runOnUiThread(this::loadRewardedAd));
    }

    /** Hide the status and navigation bars so the game uses the whole screen. */
    private void enterFullScreen() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.systemBars());
            }
        } else {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) enterFullScreen();
    }

    /** Keep the page clear of the notch and of the bars when they are briefly shown. */
    private void readSafeArea(WindowInsets insets) {
        int top = 0;
        int bottom = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Insets bars = insets.getInsets(
                    WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
            top = bars.top;
            bottom = bars.bottom;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DisplayCutout cutout = insets.getDisplayCutout();
            if (cutout != null) top = cutout.getSafeInsetTop();
            bottom = insets.getSystemWindowInsetBottom();
        } else {
            top = insets.getSystemWindowInsetTop();
            bottom = insets.getSystemWindowInsetBottom();
        }
        if (top != safeTop || bottom != safeBottom) {
            safeTop = top;
            safeBottom = bottom;
            applySafeArea();
        }
    }

    private void applySafeArea() {
        if (webView == null) return;
        String js = "if(document.documentElement){"
                + "document.documentElement.style.setProperty('--sat','" + safeTop + "px');"
                + "document.documentElement.style.setProperty('--sab','" + safeBottom + "px');"
                + "}";
        webView.evaluateJavascript(js, null);
    }

    private String rewardedUnitId() {
        return USE_TEST_ADS ? TEST_REWARDED_ID : PRODUCTION_REWARDED_ID;
    }

    private void loadRewardedAd() {
        if (isFinishing()) return;
        RewardedAd.load(this, rewardedUnitId(), new AdRequest.Builder().build(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        rewardedAd = null;
                    }
                });
    }

    private void showRewardedAd(String purpose) {
        runOnUiThread(() -> {
            if (adShowing) return;
            pendingPurpose = "hint".equals(purpose) ? "hint" : "heart";
            if (rewardedAd == null) {
                notifyWebReward(false);
                loadRewardedAd();
                Toast.makeText(this, "Ad is loading. Please try again shortly.", Toast.LENGTH_SHORT).show();
                return;
            }

            RewardedAd adToShow = rewardedAd;
            rewardedAd = null;
            adShowing = true;
            final boolean[] earned = {false};

            adToShow.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    adShowing = false;
                    enterFullScreen();
                    if (!earned[0]) notifyWebReward(false);
                    loadRewardedAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                    adShowing = false;
                    enterFullScreen();
                    notifyWebReward(false);
                    loadRewardedAd();
                }
            });

            adToShow.show(MainActivity.this, (OnUserEarnedRewardListener) rewardItem -> {
                earned[0] = true;
                notifyWebReward(true);
            });
        });
    }

    private void notifyWebReward(boolean earned) {
        if (webView == null) return;
        final String js = "window.onRewardedAdResult && window.onRewardedAdResult("
                + earned + ",\"" + pendingPurpose + "\");";
        runOnUiThread(() -> webView.evaluateJavascript(js, null));
    }

    private final class AdsBridge {
        @JavascriptInterface
        public void showRewardedAd(String purpose) {
            MainActivity.this.showRewardedAd(purpose);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            webView.evaluateJavascript("document.dispatchEvent(new KeyboardEvent('keydown',{key:'Escape'}));", null);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface("AndroidAds");
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
