
package com.reactlibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;

public class RNNativeLogsModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public RNNativeLogsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNNativeLogs";
    }

    private File getFile(String fileIdentifier) {
        File path = this.reactContext.getFilesDir();
        String pathname = path + "/" + fileIdentifier + ".txt";
        return new File(pathname);
    }

    @ReactMethod
    public void setUpRedirectLogs(String fileIdentifier, String logLevel, final Promise promise) {
        try {
            File file = getFile(fileIdentifier);
            file.createNewFile();
            int pid = android.os.Process.myPid();
            StringBuilder commandBuilder = new StringBuilder();
            commandBuilder.append("logcat -v time -f ").append(file.getAbsolutePath()).append(" --pid=").append(pid);

            if (logLevel != null) {
                String packagePrefix = reactContext.getPackageName() + ":";
                commandBuilder.append(" *:").append(logLevel).append(" ").append(packagePrefix).append(logLevel);
            }

            String command = commandBuilder.toString();
            Runtime.getRuntime().exec(command);
            promise.resolve(true);
        } catch (IOException e) {
            promise.reject("Error when redirecting logs", String.valueOf(e));
        }
    }

    @ReactMethod
    public void setUpRedirectLogs(String fileIdentifier, final Promise promise) {
        setUpRedirectLogs(fileIdentifier, null, promise);
    }

    private boolean isIgnoredTag(String line, ReadableArray tags) {
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.getString(i);
            if (line.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    @ReactMethod
    public void readOutputLogs(String fileIdentifier, ReadableArray tags, final Promise promise) {
        try {
            File file = getFile(fileIdentifier);
            WritableArray writableArray = new WritableNativeArray();
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);

            String line;
            while ((line = br.readLine()) != null) {
                if (tags == null || !isIgnoredTag(line, tags)) {
                    writableArray.pushString(line);
                }
            }
            br.close();
            fileReader.close();

            promise.resolve(writableArray);
        } catch (IOException e) {
            promise.reject("Error when reading logs", String.valueOf(e));
        }
    }

    @ReactMethod
    public void readOutputLogs(String fileIdentifier, final Promise promise) {
        readOutputLogs(fileIdentifier, null, promise);
    }
}
