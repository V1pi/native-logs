
package com.reactlibrary;

import android.util.Log;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
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

    private File getFile(String fileIdentifier){
        File path = this.reactContext.getFilesDir();
        String pathname = path + "/"  + fileIdentifier + ".txt";
        File file = new File(pathname);
        return file;
    }

     @ReactMethod
     public void setUpRedirectLogs(String fileIdentifier, final Promise promise) {
         try {
             File file = getFile(fileIdentifier);
             file.createNewFile();
             int pid = android.os.Process.myPid();
             Runtime.getRuntime().exec("logcat -v time -f " + file + " --pid=" + pid);
             promise.resolve(true);
         } catch (IOException e) {
             promise.reject("Error when redirecting logs", String.valueOf(e));
             Log.e("setUpRedirectLogs", "IOException executing logcat command.", e);
         }
     }

    @ReactMethod
    public void readOutputLogs(String fileIdentifier, final Promise promise) {
        try {
            File file = getFile(fileIdentifier);
            WritableArray writableArray = new WritableNativeArray();

            // FIRST WAY way
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            boolean done = false;

            while (!done) {
                final String line = br.readLine();
                done = (line == null);

                if (line != null) {
                    writableArray.pushString(line);
                }
            }
            br.close();
            fileReader.close();

            promise.resolve(writableArray);
        } catch (IOException e) {
            Log.e("ERROR", String.valueOf(e), e);
            promise.reject("Error when reading logs", e);
        }
    };

}
