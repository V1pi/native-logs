# RN Native Logs
This package aims to get native logs from Android and iOS applications, it also gets React Native logs. This can be useful in case you are using a third-party package and don't have control over the logs and you want send the logs to a server or print them in a different place than the Output (TextView).

On iOS it works redirectly all logs that normally go to the XCode Output to a file saved in the app's documents folder. When redirection is enabled, you will not be able to see the logs in the XCode Output.

On Android it works by running logcat and getting the logs based on your app's pid. The logs are redirected to a file and then the file is read.
## Getting started

`$ npm install rn-native-logs --save`

### Mostly automatic installation

`$ react-native link rn-native-logs`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `rn-native-logs` and add `RNNativeLogs.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNNativeLogs.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNNativeLogsPackage;` to the imports at the top of the file
  - Add `new RNNativeLogsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':rn-native-logs'
  	project(':rn-native-logs').projectDir = new File(rootProject.projectDir, 	'../node_modules/rn-native-logs/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':rn-native-logs')
  	```

## Usage
```javascript
import { NativeLogs } from 'rn-native-logs';

// The lib will start redirecting the logs to a file
await NativeLogs.redirectLogs()

// ...

// Returns an array containing all new logs since the previous getNewLogs call, it returns null in case there are no new logs.
await NativeLogs.getNewLogs();

// ...

// Returns a string with the path of the log file
await NativeLogs.getFilePath();
```
