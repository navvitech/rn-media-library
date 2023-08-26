package com.rnmedialibrary;

import static com.rnmedialibrary.Utils.getLimitOffset;

import android.Manifest;
import android.os.Build;
import android.os.Environment;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RnMediaLibraryModuleImpl {
  public static final String NAME = "RnMediaLibrary";
  private final ReactApplicationContext reactContext;

  private final AudioFileManager audioFileManagerImplementation;
  private final ImageFileManager imageFileManagerImplementation;
  private final FileManager fileManagerImplementation;

  RnMediaLibraryModuleImpl(ReactApplicationContext context) {
    reactContext = context;
    audioFileManagerImplementation = new AudioFileManager(context);
    imageFileManagerImplementation = new ImageFileManager(context);
    fileManagerImplementation = new FileManager(context);
  }

  public Map<String, Object> getConstants() {
    final Map<String, Object> mediaTypeConstants = new HashMap<>();
    mediaTypeConstants.put(Constants.MEDIA_TYPE_AUDIO_KEY, Constants.MEDIA_TYPE_AUDIO);
    mediaTypeConstants.put(Constants.MEDIA_TYPE_VIDEO_KEY, Constants.MEDIA_TYPE_VIDEO);
    mediaTypeConstants.put(Constants.MEDIA_TYPE_IMAGE_KEY, Constants.MEDIA_TYPE_IMAGE);

    final Map<String, Object> constant = new HashMap<>();
    constant.put(Constants.MEDIA_TYPE_KEY, mediaTypeConstants);
    return constant;
  }

  // React Native Methods
  public void getAssets(ReadableMap map, Promise promise) {
    String mediaType = Utils.getMediaType(map);
    boolean hasPalette = Utils.hasPaletteKey(map);

    if (!Utils.isReadExternalStoragePermissionGranted(mediaType, reactContext)) {
      Utils.rejectWithMessage(mediaType, promise);
      return;
    }
    try {
      ArrayList<Integer> limitOffset = getLimitOffset(map);
      int limit = limitOffset.get(0);
      int offset = limitOffset.get(1);


      switch (mediaType) {
        case Constants.MEDIA_TYPE_AUDIO: {
          String assets = audioFileManagerImplementation.getData(limit, offset, hasPalette);
          promise.resolve(assets);
          return;
        }
        case Constants.MEDIA_TYPE_IMAGE: {
          String assets = imageFileManagerImplementation.getData(limit, offset);
          promise.resolve(assets);
          return;
        }
      }
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void getFoldersAndFiles(String path, Promise promise) {
    if (!Utils.isReadExternalStoragePermissionGranted(Constants.MEDIA_TYPE_AUDIO, reactContext) || !Utils.isReadExternalStoragePermissionGranted(Constants.MEDIA_TYPE_VIDEO, reactContext) || !Utils.isReadExternalStoragePermissionGranted(Constants.MEDIA_TYPE_IMAGE, reactContext)) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        promise.reject(Constants.PERMISSION_ERROR, "Could not read files: require at least one of " + Manifest.permission.READ_MEDIA_AUDIO + " , " + Manifest.permission.READ_MEDIA_VIDEO + " , " + Manifest.permission.READ_MEDIA_IMAGES + " , " + "permissions to read files.");
      } else {
        promise.reject(Constants.PERMISSION_ERROR, "Could not read files: require " + Manifest.permission.READ_EXTERNAL_STORAGE + " permission.");
      }
    }

    String array;
    try {
      if (Objects.equals(path, "/")) {
        array = fileManagerImplementation.getAllRootDirectories();
      } else {
        array = fileManagerImplementation.getFoldersFilesInsideDirectory(path);
      }
      promise.resolve(array);
      return;
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void requestAllFileAccessPermission(Promise promise) {
    try {
      fileManagerImplementation.requestAllFileAccess();
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void checkAllFileAccessPermission(Promise promise) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (Environment.isExternalStorageManager()) {
          promise.resolve(true);
        } else {
          promise.resolve(false);
        }
      }
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void deleteAsset(String _id, Promise promise) {
    try {
      audioFileManagerImplementation.deleteAudio(_id, promise);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void shareAsset(String _id, Promise promise) {
    try {
      audioFileManagerImplementation.shareSong(_id, promise);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void getAudioFileInfo(String _id, Promise promise) {
    if (!Utils.isReadExternalStoragePermissionGranted(Constants.MEDIA_TYPE_AUDIO, reactContext)) {
      Utils.rejectWithMessage(Constants.MEDIA_TYPE_AUDIO, promise);
    }
    try {
      String audioFileInfo = audioFileManagerImplementation.getAudioFileInfo(_id);
      promise.resolve(audioFileInfo);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void deleteManyAudio(ReadableArray idList, Promise promise) {
    try {
      audioFileManagerImplementation.deleteManyAudio(idList, promise);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void getAlbums(ReadableMap map, Promise promise) {
    String mediaType = Utils.getMediaType(map);

    if (!Utils.isReadExternalStoragePermissionGranted(mediaType, reactContext)) {
      Utils.rejectWithMessage("album", promise);
      return;
    }
    try {
      if (Objects.equals(mediaType, Constants.MEDIA_TYPE_IMAGE)) {
        String imageAlbums = imageFileManagerImplementation.getImageAlbums();
        promise.resolve(imageAlbums);
      } else if (Objects.equals(mediaType, Constants.MEDIA_TYPE_AUDIO)) {
        String audioAlbums = audioFileManagerImplementation.getAudioAlbums();
        promise.resolve(audioAlbums);
      }
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void getAlbumAssets(ReadableMap map, String id, Promise promise) {
    String mediaType = Utils.getMediaType(map);
    boolean hasPalette = Utils.hasPaletteKey(map);

    if (!Utils.isReadExternalStoragePermissionGranted(mediaType, reactContext)) {
      Utils.rejectWithMessage(mediaType, promise);
      return;
    }

    try {
      if (Objects.equals(mediaType, Constants.MEDIA_TYPE_IMAGE)) {
        String albumImages = imageFileManagerImplementation.getAlbumImages(id);
        promise.resolve(albumImages);
      } else if (Objects.equals(mediaType, Constants.MEDIA_TYPE_AUDIO)) {
        String albumAudio = audioFileManagerImplementation.getAlbumAudio(id, hasPalette);
        promise.resolve(albumAudio);
      }
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void getArtists(Promise promise) {
    if (!Utils.isReadExternalStoragePermissionGranted(Constants.MEDIA_TYPE_AUDIO, reactContext)) {
      Utils.rejectWithMessage("artist", promise);
    }

    try {
      String artists = audioFileManagerImplementation.getArtists();
      promise.resolve(artists);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void getArtistAudio(String artistId, Promise promise) {

    if (!Utils.isReadExternalStoragePermissionGranted(Constants.MEDIA_TYPE_AUDIO, reactContext)) {
      Utils.rejectWithMessage(Constants.MEDIA_TYPE_AUDIO, promise);
    }

    try {
      String artistAudio = audioFileManagerImplementation.getArtistAudio(artistId);
      promise.resolve(artistAudio);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void getGenres(Promise promise) {

    if (!Utils.isReadExternalStoragePermissionGranted(Constants.MEDIA_TYPE_AUDIO, reactContext)) {
      Utils.rejectWithMessage(Constants.MEDIA_TYPE_AUDIO, promise);
    }

    try {
      String genres = audioFileManagerImplementation.getGenres();
      promise.resolve(genres);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }

  public void getGenreAudio(String genreId, ReadableMap map, Promise promise) {
    if (!Utils.isReadExternalStoragePermissionGranted(Constants.MEDIA_TYPE_AUDIO, reactContext)) {
      Utils.rejectWithMessage(Constants.MEDIA_TYPE_AUDIO, promise);
    }
    try {
      ArrayList<Integer> limitOffset = getLimitOffset(map);
      int limit = limitOffset.get(0);
      int offset = limitOffset.get(1);

      String genreAudio = audioFileManagerImplementation.getGenreAudio(genreId, limit, offset);
      promise.resolve(genreAudio);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, e.getMessage(), e);
    }
  }
}
