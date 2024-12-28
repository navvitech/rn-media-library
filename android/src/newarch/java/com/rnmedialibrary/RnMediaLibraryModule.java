package com.rnmedialibrary;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.util.Map;

public class RnMediaLibraryModule extends NativeRnMediaLibrarySpec {
  private RnMediaLibraryModuleImpl implementation;

  RnMediaLibraryModule(ReactApplicationContext context) {
    super(context);
    implementation = new RnMediaLibraryModuleImpl(context);
  }
  
  @Override
  @NonNull
  public String getName() {
    return RnMediaLibraryModuleImpl.NAME;
  }

  @Override
  public void getAssets(ReadableMap assetConfig, Promise promise){
    implementation.getAssets(assetConfig, promise);
  }

  @Override
  public Map<String, Object> getTypedExportedConstants() {
    return implementation.getConstants();
  }

  @Override
  public void getAudioFileInfo(String _id, Promise promise) {
    implementation.getAudioFileInfo(_id, promise);
  }

  @Override
  public void getAlbums(ReadableMap map, Promise promise) {
    implementation.getAlbums(map, promise);
  }

  @Override
    public void getAlbumAssets(ReadableMap map, String id, Promise promise) {
      implementation.getAlbumAssets(map, id, promise);
  }

  @Override
  public void getArtists(Promise promise) {
    implementation.getArtists(promise);
  }

  @Override
  public void getArtistAudio(String artistId, Promise promise) {
    implementation.getArtistAudio(artistId, promise);
  }

  @Override
  public void getGenres(Promise promise) {
    implementation.getGenres(promise);
  }

  @Override
  public void getGenreAudio(String genreId, ReadableMap map, Promise promise) {
    implementation.getGenreAudio(genreId, map, promise);
  }

  @Override
  public void getFoldersAndFiles(String path, Promise promise) {
    implementation.getFoldersAndFiles(path, promise);
  }
}
