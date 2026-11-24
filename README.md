# J.A.R.V.I.S Android Edition

This is an Android Studio source project rebuilt from the uploaded Windows/Python J.A.R.V.I.S repository, with a mobile HUD inspired by the supplied reference image.

## Included
- Iron-Man-style dark HUD using your supplied wallpaper image
- Live clock/date and cyan Arc-Reactor animation
- Android native speech recognition and text-to-speech bridge
- Voice commands: time, date, open YouTube, open Google, Google search, Wikipedia search, jokes, greeting and status
- Android app project (Java + WebView)

## Build APK
1. Install Android Studio (recent version) and Android SDK Platform 35.
2. Open this folder in Android Studio.
3. Allow Gradle sync.
4. Connect an Android phone with USB debugging enabled, or use an emulator.
5. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. Android Studio will create the debug APK under `app/build/outputs/apk/debug/`.

## Important limitations
The original repository targets desktop Windows/Python and includes Windows-only dependencies such as PyAudio wheels, PyAutoGUI, pywin32, OpenCV face-recognition setup, and PC shutdown/screenshot operations. Those are not portable to Android. This rebuild implements a mobile-native starter version; it does not claim feature parity for desktop-only face recognition, email credentials, YouTube downloading, PC controls, or all original Python modules. Voice recognition may require an internet connection and an Android speech recognition service.

## Package
`com.apurba.jarvis` — app name: J.A.R.V.I.S

## Build APK automatically with GitHub Actions
1. Create a new GitHub repository and upload the contents of this project ZIP.
2. Open the repository's **Actions** tab.
3. Select **Build JARVIS Android APK** and click **Run workflow** (or push to `main`/`master`).
4. When the workflow finishes, open its run and download the `JARVIS-debug-APK` artifact.
5. Extract the artifact ZIP to get `app-debug.apk`, then install it on your Android phone. You may need to allow installation from your browser/file manager.
