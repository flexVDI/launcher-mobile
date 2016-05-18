## launcher-mobile

**launcher-mobile** is a cross-platform mobile (iOS and Android) client for both **flexVDI Virtual Desktops** and **Generic SPICE sessions**.

This application is available in binary form as **flexVDI Client** at both the **App Store** and the **Play Store**:

[![App Store](http://depot.flexvdi.com/sources/Download_on_the_App_Store_Badge_US-UK_135x40.svg)](https://itunes.apple.com/us/app/flexvdi-client/id1051361263?mt=8)

[![Play Store](https://depot.flexvdi.com/sources/badge_google_play.png)](https://play.google.com/store/apps/details?id=com.flexvdi.androidlauncher)

### Building

#### Dependencies

The easiest way to get the dependencies needed for both apps, is to use our [Cerbero fork](https://github.com/flexVDI/cerbero). Something like this should do the trick:

 * For both platforms
```shell
mkdir source
cd source
git clone https://github.com/flexvdi/cerbero
cd cerbero
```

 * For iOS
```shell
./cerbero-uninstalled -c config/cross-ios-universal.cbc bootstrap
./cerbero-uninstalled -c config/cross-ios-universal.cbc build spiceglue
```

 * For Android
```
./cerbero-uninstalled -c config/cross-android-armv7.cbc bootstrap
./cerbero-uninstalled -c config/cross-android-armv7.cbc build spiceglue
./cerbero-uninstalled -c config/cross-android-x86.cbc bootstrap
./cerbero-uninstalled -c config/cross-android-x86.cbc build spiceglue
```

#### Building the iOS App

 * Open the iOSLauncher project with Xcode
 * In **Frameworks**, replace all static library dependencies, with the ones built with Cerbero
 * Build the project


#### Building the Android App

 * Go to **AndroidLauncher/app/jni/deps**, and for each architecture, copy the files indicated in the *README* file, from your Cerbero binary path, to those directories.
 * From **AndroidLauncher/app/jni**, run `ndk-build`
 * From **AndroidLauncher/app**, run `./gradlew nativeLibsToJar`
 * Open the **AndroidLauncher** project with AndroidStudio, and build the app

### Limitations

 * This client assumes that the **SPICE Guest Tools** are installed in the Guest, and that this one is able to adjust the screen resolution properly. If that's not the case, the screen is only shown if the device resolution is higher than the one currently configured in the Guest.

 * Though support for multiple keyboard maps is implemented, currently only the American (en_US) and the Spanish (es_ES) ones are implemented. Configure your Guest to use one of those.
   * This doesn't apply to Android with a physical keybaord, as in that case, scan codes are sent directly without processing.
   * You're more than welcome to submit a PR with an additional keymap ;-)
 
### License

**launcher-mobile** is distributed under the terms of the GNU General Public License 2.0, or (at your option) any later version. Please read [COPYING](https://github.com/flexVDI/launcher-mobile/blob/master/COPYING)

