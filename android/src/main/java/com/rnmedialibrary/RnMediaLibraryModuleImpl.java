package com.rnmedialibrary;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.google.gson.Gson;

public class RnMediaLibraryModuleImpl {
  public static final String NAME = "RnMediaLibrary";
  private final AudioFileManager audioFileManagerImplementation;
  private final ImageFileManager imageFileManagerImplementation;

  private final Gson gson = new Gson();

  RnMediaLibraryModuleImpl(ReactApplicationContext context) {
    audioFileManagerImplementation = new AudioFileManager(context);
    imageFileManagerImplementation = new ImageFileManager(context);
  }

  // React Native Methods
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
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
    }
  }

  public void deleteAsset(String _id, Promise promise) {
    try {
      audioFileManagerImplementation.deleteAudio(_id, promise);
    } catch (RuntimeException runtimeException) {
      promise.reject(Constants.ERROR_UNABLE_TO_DELETE_ASSET, "Could not delete asset: require WRITE_EXTERNAL_STORAGE permission.", runtimeException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e);
    }
  }

  public void shareAsset(String _id, Promise promise) {
    audioFileManagerImplementation.shareSong(_id, promise);
  }

  public void getAudioFileInfo(String _id, Promise promise) {
    try {
      AudioFileInfo audioFileInfo = audioFileManagerImplementation.getAudioFileInfo(_id);
      String json = gson.toJson(audioFileInfo);
      promise.resolve(json);
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read audio file info", e);
    }
  }

  public void deleteManyAudio(ReadableArray idList, Promise promise) {
    try {
      audioFileManagerImplementation.deleteManyAudio(idList, promise);
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read audio file info", e);
    }
  }
}
