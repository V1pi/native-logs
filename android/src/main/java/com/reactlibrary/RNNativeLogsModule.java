
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

    @ReactMethod
    public void setUpRedirectLogs(String fileIdentifier, final Promise promise) {
        try {
            File path = this.reactContext.getFilesDir();
            String pathname = path + "/"  + fileIdentifier + ".txt";
            File file = new File(pathname);
            file.createNewFile();
            Log.e("redirectLogs", pathname);
            Runtime.getRuntime().exec("logcat -v time -f " + pathname + " -s vouchkeymanuap");
            promise.resolve(true);
        } catch (IOException e) {
            promise.reject("Error when redirecting logs",e);
            Log.e("LOGTAG", "IOException executing logcat command.", e);
        }
    }

    @ReactMethod
    public void readOutputLogs(String fileIdentifier, final Promise promise) {
        //Get the text file
        File path = this.reactContext.getFilesDir();
        String pathname = path + "/" + fileIdentifier + ".txt";
        File file = new File(pathname);

        //Read text from file
        StringBuilder textArray = new StringBuilder();
        WritableArray writableArray = new WritableNativeArray();

        try {
//            // A DIFFERENT WAY TO GE THE TEXT Logs
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                String content = new String(Files.readAllBytes(Paths.get(pathname)));
//                String[] linesArray = content.split("\\r?\\n");
//                for(int i = 0; i < linesArray.length; i++)
//                {
//                    writableArray.pushString(linesArray[i]);
//                }
//            }

            // FIRST WAY way 
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            Log.e(" BufferedReader;", " bBufferedReader;");

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
