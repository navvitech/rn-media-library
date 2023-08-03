package com.rnmedialibrary;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.google.gson.Gson;

public class RnMediaLibraryModuleImpl {
  public static final String NAME = "RnMediaLibrary";
  private AudioFileManager audioFileManagerImplementation;
  private ImageFileManager imageFileManagerImplementation;

  private Gson gson = new Gson();

  RnMediaLibraryModuleImpl(ReactApplicationContext context) {
    audioFileManagerImplementation = new AudioFileManager(context);
    imageFileManagerImplementation = new ImageFileManager(context);
  }

  // React Native Methods
  public void add(double a, double b, Promise promise) {
    promise.resolve(a + b);
  }

  public void getAssets(ReadableMap map, Promise promise) {
    try {
      double limit = 50, offset = 0;
      String mediaType = "audio";
      if (map.hasKey("limit")) {
        limit = map.getDouble("limit");
      }
      if (map.hasKey("offset")) {
        offset = map.getDouble("offset");
      }
      if (map.hasKey("mediaType")) {
        mediaType = map.getString("mediaType");
      }

      switch (mediaType) {
        case "audio": {
          Data<AudioFile> assets = audioFileManagerImplementation.getData(limit, offset);
          String json = gson.toJson(assets);
          promise.resolve(json);
          return;
        }
        case "image": {
          Data<ImageFile> assets = imageFileManagerImplementation.getData(limit, offset);
          String json = gson.toJson(assets);
          promise.resolve(json);
          return;
        }
      }
    } catch (Exception e) {
      Log.e("DEBUG", "ERROR: " + e.toString());
      promise.reject("Failed to fetch audio files", e);
    }
  }
}
