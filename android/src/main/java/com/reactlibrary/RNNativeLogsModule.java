
package com.reactlibrary;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;

public class RNNativeLogsModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private BufferedReader reader = null;
    private static final int MAX_LINES = 64;

    private final String FILE_NAME = String.format(Locale.US, "%s-%s.txt", "NativeLogs",
            UUID.randomUUID().getLeastSignificantBits() & Long.MAX_VALUE);

    public RNNativeLogsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNNativeLogs";
    }

    private File getFile() {
        File path = this.reactContext.getFilesDir();
        String pathname = path + "/" + FILE_NAME + ".txt";
        return new File(pathname);
    }

    @ReactMethod
    public void setUpRedirectLogs(ReadableArray tags, final Promise promise) {
        try {
            if(reader != null) {
                promise.resolve(true);
                return;
            }
            File file = getFile();
            int pid = android.os.Process.myPid();
            StringBuilder commandBuilder = new StringBuilder();
            commandBuilder.append(String.format(Locale.US,"logcat -v time -f %s --pid=%d", file.getAbsolutePath(), pid));

            if (tags != null && tags.size() > 0) {
                for (int i = 0; i < tags.size(); i++) {
                    String tag = tags.getString(i);
                    commandBuilder.append(String.format(Locale.US, " %s", tag));
                }
            }

            String command = commandBuilder.toString();
            Runtime.getRuntime().exec(command);
            promise.resolve(true);
        } catch (IOException e) {
            promise.reject("Error when redirecting logs", String.valueOf(e));
        }
    }

    @ReactMethod
    public void setUpRedirectLogs(final Promise promise) {
        setUpRedirectLogs(null, promise);
    }

    @ReactMethod
    public void readOutputLogs(final Promise promise) {
        try {
            if (reader == null) {
                File file = getFile();
                FileReader fileReader = new FileReader(file);
                reader = new BufferedReader(fileReader);
            }
            WritableArray writableArray = new WritableNativeArray();
            String line;
            int count = 0;

            while (count < MAX_LINES && (line = Objects.requireNonNull(reader).readLine()) != null) {
                writableArray.pushString(line);
                count++;
            }

            promise.resolve(writableArray);
        } catch (IOException e) {
            promise.reject("Error when reading logs", String.valueOf(e));
        }
    }

    @ReactMethod
    public void getFilePath(final Promise promise) {
        promise.resolve(getFile().getAbsolutePath());
    }
}
