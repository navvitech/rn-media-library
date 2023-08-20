package com.rnmedialibrary;

public class ImageFile {
  private final String _id;
  private final String displayName;
  private final String uri;
  private final String size;
  private final String mimeType;
  private final String resolution;
  private final String dateAdded;
  private final String dateModified;
  private final String width;
  private final String height;

   ImageFile(String _id, String displayName, String uri, String size, String mimeType, String resolution, String dateAdded, String dateModified, String width, String height) {
    this._id = _id;
    this.displayName = displayName;
    this.uri = uri;
    this.size = size;
    this.mimeType = mimeType;
    this.resolution = resolution;
    this.dateAdded = dateAdded;
    this.dateModified = dateModified;
    this.width = width;
    this.height = height;
  }
}
