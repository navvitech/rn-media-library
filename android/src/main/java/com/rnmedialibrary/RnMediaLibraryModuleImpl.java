package com.rnmedialibrary;

import static com.rnmedialibrary.Utils.getLimitOffset;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.google.gson.Gson;

import java.util.ArrayList;

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
      ArrayList<Integer> limitOffset = getLimitOffset(map);
      int limit = limitOffset.get(0);
      int offset = limitOffset.get(1);

      String mediaType = "audio";

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

  public void getAudioAlbums(Promise promise) {
    try {
      ArrayList<AudioAlbum> audioAlbums = audioFileManagerImplementation.getAudioAlbums();
      String json = gson.toJson(audioAlbums);
      promise.resolve(json);
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
    }
  }

  public void getAlbumAudio(String albumId, Promise promise) {
    try {
      Data<AudioFile> albumAudio = audioFileManagerImplementation.getAlbumAudio(albumId);
      String json = gson.toJson(albumAudio);
      promise.resolve(json);
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
      ArrayList<Artist> artists = audioFileManagerImplementation.getArtists();
      String json = gson.toJson(artists);
      promise.resolve(json);
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
      Data<AudioFile> artistAudio = audioFileManagerImplementation.getArtistAudio(artistId);
      String json = gson.toJson(artistAudio);
      promise.resolve(json);
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
      ArrayList<Genre> genres = audioFileManagerImplementation.getGenres();
      String json = gson.toJson(genres);
      promise.resolve(json);
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

      Data<AudioFile> genreAudio = audioFileManagerImplementation.getGenreAudio(genreId,limit,offset);
      String json = gson.toJson(genreAudio);
      promise.resolve(json);
    } catch (SecurityException securityException) {
      promise.reject(Constants.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not read assets: require READ_EXTERNAL_STORAGE permission.", // need to edit message according to
        // android versions
        securityException);
    } catch (Exception e) {
      promise.reject(Constants.UNKNOWN_ERROR, "Failed to read assets", e);
    }
  }
}
