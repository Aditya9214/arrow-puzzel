# Arrow Puzzel — Pixel Edition (Free Path — Android Studio project)

Full pixel-art remake of the game, wrapped in an Android WebView with native AdMob ads.

## What the Pixel Edition is

- **Pure pixel art, end to end.** Chunky low-res board rendering upscaled with crisp nearest-neighbour pixels, a hand-drawn pixel sky (twinkling stars, drifting clouds, a cratered moon, occasional shooting stars), pixel SVG icons and hearts, hard-shadow retro buttons, and **Press Start 2P embedded as a base64 font** — the app ships zero external assets and works fully offline.
- **Denser levels.** Boards ramp from ~54 arrows (Lv 5) to 300+ (late Extreme tiers, up to a 52-wide board); the generator still guarantees every board is solvable at spawn.
- **Offline original soundtrack + SFX.** Two chiptune loops (calm menu, chill in-game) plus a full retro SFX set (chain-blip taps coin sparkles, block buzz, win fanfare, refill jingle) are synthesised live with Web Audio — no files, no licensing, no network. Tones and music have separate toggles.
- **Rewards that keep you playing.** Coin-rush chain bar, hit-stop and slow-mo on big chains, idle hint pulses after 20 s, confetti stars on every clear, streak calendar and a live teaser of today's board on the Challenge page.
- **Brand-new Challenge page:** pixel trophy, date ribbon, live midnight countdown, streak card, reward chest line, and an animated preview of the actual seeded daily board.
- **Reworked Collection:** 48 items across arrows / heads / trails / boards / frames / tones / titles, all drawn as pixel previews, with rarity rails and unlock/reveal animation.

## Lives, hints, and ads

- 3 hearts, 3 hints per run. Blocked tap = −1 heart; hint button reveals a free arrow.
- If you run out of hearts: **Watch ad → +3 hearts** and continue the same board.
- When hints are out: **Watch ad → +1 hint**.
- Every 3 cleared levels one **interstitial ad** shows between levels (per AdMob policy: never before the first clear).
- Rewards are granted only from the native `OnUserEarnedRewardListener`.
- **Test ads are enabled by default** (`USE_TEST_ADS`). Insert your real interstitial unit ID in `MainActivity.java` (`PRODUCTION_INTERSTITIAL_ID`) and flip to `false` before store release.

## Included

- `app/src/main/assets/index.html` — the whole game (single file, fully offline).
- Android WebView wrapper with localStorage, immersive fullscreen, and notch insets passed to the page.
- Native AdMob bridge: `showRewardedAd(purpose)` and `showInterstitial()`; JS callbacks `onRewardedAdResult`, `onAdOpened/onAdClosed`, `onInterstitialOpened/onInterstitialClosed` so the page can duck its music during ads.
- Each push to `main` builds a debug APK and publishes it to the rolling `debug-latest` release, signed with the fixed key in `app/ci-debug-key.p12` so updates install over previous builds.

## Open and build

1. Install Android Studio and a JDK 17-compatible setup.
2. Open the project in Android Studio, allow Gradle sync (compile SDK 36).
3. Run on a device/emulator. Keep `USE_TEST_ADS = true` while developing.
4. Local dev tip: the game also runs directly in a desktop browser — open `index.html`.

## Download the latest build

https://github.com/Aditya9214/arrow-puzzel/releases/download/debug-latest/FreePath-debug.apk

## IDs

- App ID: `ca-app-pub-5458302282891714~4066016240`
- Rewarded production unit: `ca-app-pub-5458302282891714/8631947211`
- Interstitial: test unit used in dev; add the production unit before release
- Test rewarded unit (dev): `ca-app-pub-3940256099942544/5224354917`

## Version

- `versionCode 7`, `versionName 2.0` (Pixel Edition)
