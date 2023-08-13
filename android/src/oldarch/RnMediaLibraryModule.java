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

}
