# Free Path — Android Studio project

This project wraps the supplied single-file HTML puzzle game in an Android WebView and connects its **Watch ad +1 heart** action to a native Google Mobile Ads rewarded ad.

## Included
- Original game copied to `app/src/main/assets/index.html` with the fake 5-second demo reward removed.
- Android WebView wrapper with localStorage enabled and immersive fullscreen (status/nav bars hidden, notch insets passed to the page).
- Native AdMob rewarded ad bridge, used for two rewards: **+1 heart** after running out of lives and **+1 hint** once the two free hints are used. Rewards are granted only from `OnUserEarnedRewardListener`.
- **Coin economy:** clearing a level pays coins (base + a bonus per heart left), and every arrow cleared during the coin-rush cooldown bar pays extra coins — the longer you keep the chain alive, the bigger the payout.
- **Collection shop:** arrows, arrowheads, trails, boards, frames, sounds and titles are unlocked with coins, previewed on a redesigned Frutiger Aero / Y2K glass UI. Purchases and the equipped loadout persist in localStorage.
- Your AdMob App ID is configured in the manifest resources.
- Your rewarded Ad Unit ID is saved in `MainActivity.java` for release use.
- **Test ads are enabled by default** (`USE_TEST_ADS`). Do not test with live ads.
- Each push to `main` builds a debug APK and publishes it to the rolling `debug-latest` release, signed with the fixed key in `app/ci-debug-key.p12` so updates install over the previous build.

## Open and build
1. Install Android Studio and a JDK 17-compatible Android Studio version.
2. Extract this folder and open `FreePathAndroid` in Android Studio.
3. Allow Gradle sync and install the Android SDK requested by the project (compile SDK 36).
4. Run on a physical Android phone or emulator to test the game. Rewarded ads may not load in every emulator/network on the first attempt.
5. For local development, keep `USE_TEST_ADS = true` in `MainActivity.java`.
6. Only when you are ready to publish, have the account owner verify the AdMob app setup and consent/privacy requirements, then change `USE_TEST_ADS` to `false`. Build a signed release AAB from **Build → Generate Signed Bundle / APK**.

## Download the latest build
https://github.com/Aditya9214/arrow-puzzel/releases/download/debug-latest/FreePath-debug.apk

## Current scope / limitations
- Rewarded ads for +1 heart and +1 hint are integrated.
- Coins are earned only by playing (levels, daily challenge and fast-clear chains); there is no way to buy coins and ads do not grant them.
- The collection UI and coin changes live entirely in `app/src/main/assets/index.html`, so the Android wrapper and ad bridge are unchanged.
- Interstitial ads are **not** included because no interstitial ad unit ID was supplied.
- Debug builds are signed with the debug-only key `app/ci-debug-key.p12`; a Play Store release still needs its own upload key.

## IDs
- App ID: `ca-app-pub-5458302282891714~4066016240`
- Rewarded production unit: `ca-app-pub-5458302282891714/8631947211`
- Google test rewarded unit used during development: `ca-app-pub-3940256099942544/5224354917`
