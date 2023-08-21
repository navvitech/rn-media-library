package com.rnmedialibrary;

import static com.rnmedialibrary.Utils.getLimitOffset;

import android.os.Build;
import android.os.Environment;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;
import java.util.Objects;

public class RnMediaLibraryModuleImpl {
  public static final String NAME = "RnMediaLibrary";
  private final AudioFileManager audioFileManagerImplementation;
  private final ImageFileManager imageFileManagerImplementation;
  private final FileManager fileManagerImplementation;

  RnMediaLibraryModuleImpl(ReactApplicationContext context) {
    audioFileManagerImplementation = new AudioFileManager(context);
    imageFileManagerImplementation = new ImageFileManager(context);
    fileManagerImplementation = new FileManager(context);
  }

  // React Native Methods
  public void getAssets(ReadableMap map, Promise promise) {
    try {
      ArrayList<Integer> limitOffset = getLimitOffset(map);
      int limit = limitOffset.get(0);
      int offset = limitOffset.get(1);

      String mediaType = "audio";

      if (map.hasKey("mediaType")) {
        mediaType = map.getString("mediaType");
      }

      switch (mediaType) {
        case "audio": {
          String assets = audioFileManagerImplementation.getData(limit, offset);
          promise.resolve(assets);
          return;
        }
        case "image": {
          String assets = imageFileManagerImplementation.getData(limit, offset);
          promise.resolve(assets);
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

  public void getFoldersAndFiles(String path, Promise promise) {
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
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
    }
  }

  public void requestAllFileAccessPermission(Promise promise) {
    try {
      fileManagerImplementation.requestAllFileAccess();
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
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
    try {
      audioFileManagerImplementation.shareSong(_id, promise);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read audio file info", e);
    }
  }

  public void getAudioFileInfo(String _id, Promise promise) {
    try {
      String audioFileInfo = audioFileManagerImplementation.getAudioFileInfo(_id);
      promise.resolve(audioFileInfo);
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

  public void getAlbums(ReadableMap map, Promise promise) {
    try {
      String mediaType = "image";
      if (map.hasKey("mediaType")) {
        mediaType = map.getString("mediaType");
      }
      if (Objects.equals(mediaType, "image")) {
        String imageAlbums = imageFileManagerImplementation.getImageAlbums();
        promise.resolve(imageAlbums);
      } else if (Objects.equals(mediaType, "audio")) {
        String audioAlbums = audioFileManagerImplementation.getAudioAlbums();
        promise.resolve(audioAlbums);
      }
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
    }
  }

  public void getAlbumAssets(ReadableMap map, String id, Promise promise) {
    try {
      String mediaType = "image";
      if (map.hasKey("mediaType")) {
        mediaType = map.getString("mediaType");
      }
      if (Objects.equals(mediaType, "image")) {
        String albumImages = imageFileManagerImplementation.getAlbumImages(id);
        promise.resolve(albumImages);
      } else if (Objects.equals(mediaType, "audio")) {
        String albumAudio = audioFileManagerImplementation.getAlbumAudio(id);
        promise.resolve(albumAudio);
      }
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
    }
  }

  public void getArtists(Promise promise) {
    try {
      String artists = audioFileManagerImplementation.getArtists();
      promise.resolve(artists);
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
    }
  }

  public void getArtistAudio(String artistId, Promise promise) {
    try {
      String artistAudio = audioFileManagerImplementation.getArtistAudio(artistId);
      promise.resolve(artistAudio);
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
    }
  }

  public void getGenres(Promise promise) {
    try {
      String genres = audioFileManagerImplementation.getGenres();
      promise.resolve(genres);
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
    }
  }

  public void getGenreAudio(String genreId, ReadableMap map, Promise promise) {
    try {
      ArrayList<Integer> limitOffset = getLimitOffset(map);
      int limit = limitOffset.get(0);
      int offset = limitOffset.get(1);

      String genreAudio = audioFileManagerImplementation.getGenreAudio(genreId, limit, offset);
      promise.resolve(genreAudio);
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
    }
  }
}
