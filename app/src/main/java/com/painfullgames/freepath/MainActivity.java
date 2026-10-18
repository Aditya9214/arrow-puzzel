package com.painfullgames.freepath;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
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

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(false);
        webView.addJavascriptInterface(new AdsBridge(), "AndroidAds");
        webView.loadUrl("file:///android_asset/index.html");

        MobileAds.initialize(this, initializationStatus -> runOnUiThread(this::loadRewardedAd));
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

    private void showRewardedAd() {
        runOnUiThread(() -> {
            if (adShowing) return;
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
                    if (!earned[0]) notifyWebReward(false);
                    loadRewardedAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                    adShowing = false;
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
        final String js = "window.onRewardedAdResult && window.onRewardedAdResult(" + earned + ");";
        runOnUiThread(() -> webView.evaluateJavascript(js, null));
    }

    private final class AdsBridge {
        @JavascriptInterface
        public void showRewardedAd() {
            MainActivity.this.showRewardedAd();
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
