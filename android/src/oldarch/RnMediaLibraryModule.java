package com.rnmedialibrary;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
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
  public void add(int a, int b, Promise promise) {
    implementation.add(a, b, promise);
  }

  @ReactMethod
  public void getAssets(ReadableMap assetConfig, Promise promise) {
    implementation.getAssets(assetConfig, promise);
  }
}
