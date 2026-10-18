# Free Path — Android Studio project

This project wraps the supplied single-file HTML puzzle game in an Android WebView and connects its **Watch ad +1 heart** action to a native Google Mobile Ads rewarded ad.

## Included
- Original game copied to `app/src/main/assets/index.html` with the fake 5-second demo reward removed.
- Android WebView wrapper with localStorage enabled.
- Native AdMob rewarded ad bridge. The heart is awarded only from `OnUserEarnedRewardListener`.
- Your AdMob App ID is configured in the manifest resources.
- Your rewarded Ad Unit ID is saved in `MainActivity.java` for release use.
- **Test ads are enabled by default**. Do not test with live ads.

## Open and build
1. Install Android Studio and a JDK 17-compatible Android Studio version.
2. Extract this folder and open `FreePathAndroid` in Android Studio.
3. Allow Gradle sync and install the Android SDK requested by the project (compile SDK 36).
4. Run on a physical Android phone or emulator to test the game. Rewarded ads may not load in every emulator/network on the first attempt.
5. For local development, keep `USE_TEST_ADS = true` in `MainActivity.java`.
6. Only when you are ready to publish, have the account owner verify the AdMob app setup and consent/privacy requirements, then change `USE_TEST_ADS` to `false`. Build a signed release AAB from **Build → Generate Signed Bundle / APK**.

## Current scope / limitations
- Rewarded ad for +1 heart is integrated.
- Interstitial ads are **not** included because no interstitial ad unit ID was supplied.
- This environment did not have Android SDK/Gradle installed, so this is a ready-to-open Android Studio source project, not a compiled APK/AAB.

## IDs
- App ID: `ca-app-pub-5458302282891714~4066016240`
- Rewarded production unit: `ca-app-pub-5458302282891714/8631947211`
- Google test rewarded unit used during development: `ca-app-pub-3940256099942544/5224354917`
