package com.rnmedialibrary;

import android.net.Uri;

public class ImageFile {
  private final String _id;
  private final String displayName;
  private final String path;
  private final String size;
  private final String mimeType;
  private final String resolution;
  private final String dateAdded;
  private final String dateModified;

  public ImageFile(String _id, String displayName, Uri path, String size, String mimeType, String resolution,
      String dateAdded, String dateModified) {
    this._id = _id;
    this.displayName = displayName;
    this.path = String.valueOf(path);
    this.size = size;
    this.mimeType = mimeType;
    this.resolution = resolution;
    this.dateAdded = dateAdded;
    this.dateModified = dateModified;
  }
}
