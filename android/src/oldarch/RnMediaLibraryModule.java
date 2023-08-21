package com.rnmedialibrary;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

public class RnMediaLibraryModule extends ReactContextBaseJavaModule {

  private RnMediaLibraryModuleImpl implementation;

  RnMediaLibraryModule(ReactApplicationContext context) {
    super(context);
    implementation = new RnMediaLibraryModuleImpl(context);
  }

  @Override
  public String getName() {
    return RnMediaLibraryModuleImpl.NAME;
  }

  @ReactMethod
  public void getAssets(ReadableMap assetConfig, Promise promise) {
    implementation.getAssets(assetConfig, promise);
  }

  @ReactMethod
  public void deleteAsset(String _id, Promise promise) {
    implementation.deleteAsset(_id, promise);
  }

  @ReactMethod
  public void shareAsset(String _id, Promise promise) {
    implementation.shareAsset(_id, promise);
  }

  @ReactMethod
  public void getAudioFileInfo(String _id, Promise promise) {
    implementation.getAudioFileInfo(_id, promise);
  }

  @ReactMethod
  public void deleteManyAudio(ReadableArray idList, Promise promise) {
    implementation.deleteManyAudio(idList, promise);
  }

  @ReactMethod
  public void getAlbums(ReadableMap map, Promise promise) {
    implementation.getAlbums(map, promise);
  }

  @ReactMethod
  public void getAlbumAssets(ReadableMap map, String id, Promise promise) {
    implementation.getAlbumAssets(map, id, promise);
  }

  @ReactMethod
  public void getArtists(Promise promise) {
    implementation.getArtists(promise);
  }

  @ReactMethod
  public void getArtistAudio(String artistId, Promise promise) {
    implementation.getArtistAudio(artistId, promise);
  }

  @ReactMethod
  public void getGenres(Promise promise) {
    implementation.getGenres(promise);
  }

  @ReactMethod
  public void getGenreAudio(String genreId, ReadableMap map, Promise promise) {
    implementation.getGenreAudio(genreId, map, promise);
  }

  @ReactMethod
  public void getFoldersAndFiles(String path, Promise promise) {
    implementation.getFoldersAndFiles(path, promise);
  }

  @ReactMethod
  public void requestAllFileAccessPermission(Promise promise) {
    implementation.requestAllFileAccessPermission(promise);
  }

  @ReactMethod
  public void checkAllFileAccessPermission(Promise promise) {
    implementation.checkAllFileAccessPermission(promise);
  }
}
